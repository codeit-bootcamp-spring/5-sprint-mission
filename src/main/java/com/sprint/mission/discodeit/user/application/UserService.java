package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.auth.domain.CredentialUpdated;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.global.cache.CacheHelper;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import com.sprint.mission.discodeit.user.domain.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.user.domain.exception.DuplicateUsernameException;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.domain.exception.UserProfileUploadException;
import com.sprint.mission.discodeit.user.presentation.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.user.presentation.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractUserAgent;
import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final CacheHelper cacheHelper;
    private final ApplicationEventPublisher eventPublisher;

    @CacheEvict(value = CacheName.USERS, allEntries = true)
    @Transactional
    public UserDto create(UserCreateRequest request, MultipartFile profile) {
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

    @Cacheable(value = CacheName.USERS)
    public List<UserDto> findAll() {
        log.debug("사용자 목록 캐시 미스");
        return userRepository.findAllWithProfile()
            .stream()
            .map(userMapper::toDto)
            .toList();
    }

    @Cacheable(value = CacheName.USER, key = "#userId")
    public UserDto findById(UUID userId) {
        log.debug("사용자 조회 캐시 미스: userId={}", userId);
        User user = userRepository.findWithProfileById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toDto(user);
    }

    @PreAuthorize("authentication.principal.userDto.id == #userId")
    @Transactional
    @Caching(
        put = @CachePut(value = CacheName.USER, key = "#userId"),
        evict = @CacheEvict(value = CacheName.USERS, allEntries = true)
    )
    public UserDto update(
        UUID userId,
        UserUpdateRequest request,
        MultipartFile profile
    ) {
        User user = userRepository.findWithProfileById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

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
            if (user.getProfile() != null) {
                binaryContentRepository.delete(user.getProfile());
            }
        }

        String newEncodedPassword = null;
        if (hasText(request.newPassword())
            && !passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            newEncodedPassword = passwordEncoder.encode(request.newPassword());
            publishCredentialUpdatedEvent(user);
        }

        user.update(
            newUsername,
            newEmail,
            newEncodedPassword,
            newProfile
        );

        cacheHelper.evictCacheByKey(CacheName.USER_DETAILS, oldUsername);

        log.info("사용자 수정 완료: username={}, email={} to newUsername={}, newEmail={}",
            oldUsername, oldEmail, newUsername, newEmail);

        return userMapper.toDto(user);
    }

    @PreAuthorize("authentication.principal.userDto.id == #userId")
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheName.USER, key = "#userId"),
        @CacheEvict(value = CacheName.USERS, allEntries = true)
    })
    public void deleteById(UUID userId) {
        log.debug("사용자 삭제 요청: userId={}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        String username = user.getUsername();

        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }
        userRepository.delete(user);

        cacheHelper.evictCacheByKey(CacheName.USER_DETAILS, username);

        eventPublisher.publishEvent(new UserDeletedEvent(userId));

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
                new BinaryContentCreatedEvent(savedProfile.getId(), profile.getBytes()));
        } catch (IOException e) {
            throw new UserProfileUploadException(e);
        }

        log.info("프로필 이미지 저장 완료: binaryContentId={}, size={}",
            savedProfile.getId(), savedProfile.getSize());

        return savedProfile;
    }

    private void publishCredentialUpdatedEvent(User user) {
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                log.warn("요청 속성을 찾을 수 없습니다. 비밀번호 변경 이벤트 발행을 건너뜁니다.");
                return;
            }

            String ipAddress = extractIpAddress(attributes.getRequest());
            String userAgent = extractUserAgent(attributes.getRequest());

            eventPublisher.publishEvent(new CredentialUpdated(
                user.getId(),
                user.getUsername(),
                ipAddress,
                userAgent
            ));
        } catch (Exception e) {
            log.error("비밀번호 변경 이벤트 발행 실패: userId={}", user.getId(), e);
        }
    }
}
