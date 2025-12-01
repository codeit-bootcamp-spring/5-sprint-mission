package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserProfileUploadException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    private final UserMapper userMapper;

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserDto create(
        UserCreateRequest request,
        MultipartFile profile
    ) {
        String username = request.username().strip().toLowerCase(Locale.ROOT);
        String email = request.email().strip().toLowerCase(Locale.ROOT);

        log.info("사용자 생성 요청: username={}, email={}", username, email);

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        String password = passwordEncoder.encode(request.password());

        BinaryContent savedProfile = null;
        if (profile != null && !profile.isEmpty()) {
            savedProfile = saveProfileImage(profile);
        }

        User user = userRepository.save(
            new User(
                username,
                email,
                password,
                savedProfile
            )
        );

        log.info("사용자 생성 완료: userId={}, email={}", user.getId(), user.getEmail());

        return userMapper.toDto(user);
    }

    @Cacheable(value = "users")
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        log.debug("사용자 목록 캐시 미스");
        return userRepository.findAllGraph()
            .stream()
            .map(userMapper::toDto)
            .toList();
    }

    @CacheEvict(value = "users", allEntries = true)
    @PreAuthorize("authentication.principal.userDto.id == #userId")
    @Transactional
    public UserDto update(
        UUID userId,
        UserUpdateRequest request,
        MultipartFile profile
    ) {

        User user = getUserOrThrow(userId);

        String oldUsername = user.getUsername();
        String oldEmail = user.getEmail();
        String newUsername = null;
        if (hasText(request.newUsername())) {
            newUsername = request.newUsername().strip().toLowerCase(Locale.ROOT);
        }
        String newEmail = null;
        if (hasText(request.newEmail())) {
            newEmail = request.newEmail().strip().toLowerCase(Locale.ROOT);
        }

        log.info("사용자 수정 요청: username={}, email={} to newUsername={}, newEmail={}",
            oldUsername, oldEmail, newUsername, newEmail);

        if (newUsername != null && userRepository.existsByUsername(newUsername)) {
            throw new DuplicateUsernameException(newUsername);
        }

        if (newEmail != null && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }

        BinaryContent newProfile = null;
        if (profile != null && !profile.isEmpty()) {
            newProfile = saveProfileImage(profile);
        }

        updateUser(user, request, newProfile, newUsername, newEmail);

        evictUserDetailsCache(oldUsername);

        log.info("사용자 수정 완료: username={}, email={} to newUsername={}, newEmail={}",
            oldUsername, oldEmail, newUsername, newEmail);
        return userMapper.toDto(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @PreAuthorize("authentication.principal.userDto.id == #userId")
    @Transactional
    public void delete(UUID userId) {
        log.debug("사용자 삭제 요청: userId={}", userId);

        User user = getUserOrThrow(userId);
        String username = user.getUsername();

        messageRepository.nullifyAuthorByUser(user);
        readStatusRepository.deleteAllByUser(user);

        userRepository.delete(user);

        evictUserDetailsCache(username);

        log.info("사용자 삭제 완료: userId={}", userId);
    }

    private BinaryContent saveProfileImage(MultipartFile profile) {
        log.debug("프로필 이미지 업로드 시도: filename={}, size={}",
            profile.getOriginalFilename(), profile.getSize());

        BinaryContent savedProfile = binaryContentRepository.save(
            new BinaryContent(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()
            )
        );

        try {
            eventPublisher.publishEvent(
                new BinaryContentCreatedEvent(savedProfile.getId(), profile.getBytes())
            );
        } catch (IOException e) {
            throw new UserProfileUploadException(e);
        }

        log.info("프로필 이미지 저장 완료: binaryContentId={}, size={}",
            savedProfile.getId(), savedProfile.getSize());

        return savedProfile;
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void evictUserDetailsCache(String username) {
        Cache cache = cacheManager.getCache("userDetails");
        Objects.requireNonNull(cache).evict(username);
    }

    private void updateUser(
        User user,
        UserUpdateRequest request,
        BinaryContent newProfile,
        String newUsername,
        String newEmail
    ) {
        String newEncodedPassword = null;
        if (hasText(request.newPassword())
            && !passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            newEncodedPassword = passwordEncoder.encode(request.newPassword());
        }

        user.update(
            newUsername,
            newEmail,
            newEncodedPassword,
            newProfile
        );
    }
}
