package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.auth.domain.event.CredentialUpdatedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import com.sprint.mission.discodeit.user.domain.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.user.domain.exception.DuplicateUsernameException;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ProfileImageManager profileImageManager;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final CacheService cacheService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = CacheName.USERS, allEntries = true)
    public UserDto create(UserCreateRequest request, MultipartFile profile) {
        String username = normalize(request.username());
        String email = normalize(request.email());

        log.debug("Creating user: [username={}, email={}]", username, email);

        validateUserUniqueness(username, email);

        String encodedPassword = passwordEncoder.encode(request.password());
        BinaryContent savedProfile = profileImageManager.save(profile);

        User user = userRepository.save(new User(username, email, encodedPassword, savedProfile));

        UserDto result = userMapper.toDto(user);

        log.info("User created: [userId={}, username={}]", result.id(), result.username());

        return result;
    }

    @Cacheable(value = CacheName.USERS)
    public List<UserDto> findAll() {
        log.debug("[Cache Miss] Finding all users");

        return new ArrayList<>(userMapper.toDtoList(userRepository.findAllWithProfile()));
    }

    @Cacheable(value = CacheName.USER, key = "#userId")
    public UserDto findById(UUID userId) {
        log.debug("[Cache Miss] Finding user: [userId={}]", userId);

        User user = userRepository.findWithProfileById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toDto(user);
    }

    @PreAuthorize("authentication.principal.userDetailsDto.id == #userId")
    @Transactional
    @Caching(
        put = @CachePut(value = CacheName.USER, key = "#userId"),
        evict = @CacheEvict(value = CacheName.USERS, allEntries = true)
    )
    public UserDto update(
        UUID userId,
        UserUpdateRequest request,
        MultipartFile profile,
        String ipAddress,
        String userAgent
    ) {
        String newUsername = normalizeIfPresent(request.newUsername());
        String newEmail = normalizeIfPresent(request.newEmail());

        log.debug("Updating user: [userId={}, newUsername={}, newEmail={}]",
            userId, newUsername, newEmail);

        User user = userRepository.findWithProfileById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        String oldUsername = user.getUsername();

        if (newUsername != null && !newUsername.equals(oldUsername)
            && userRepository.existsByUsername(newUsername)) {
            throw new DuplicateUsernameException(newUsername);
        }
        if (newEmail != null && !newEmail.equals(user.getEmail())
            && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }

        user.updateInfo(newUsername, newEmail);
        BinaryContent oldProfile = user.getProfile();
        if (profile != null && !profile.isEmpty()) {
            BinaryContent newProfileImage = profileImageManager.save(profile);
            user.updateProfile(newProfileImage);
            profileImageManager.delete(oldProfile);
        }
        updatePasswordIfChanged(user, request.newPassword(), ipAddress, userAgent);

        UserDto result = userMapper.toDto(user);

        log.info("User updated: [userId={}, username={}]", result.id(), result.username());

        return result;
    }

    @PreAuthorize("authentication.principal.userDetailsDto.id == #userId")
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheName.USER, key = "#userId"),
        @CacheEvict(value = CacheName.USERS, allEntries = true)
    })
    public void deleteById(UUID userId) {
        log.debug("Deleting user: [userId={}]", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        profileImageManager.delete(user.getProfile());
        userRepository.delete(user);
        eventPublisher.publishEvent(new UserDeletedEvent(userId));

        log.info("User deleted: [userId={}]", userId);
    }

    private void validateUserUniqueness(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
    }

    private void updatePasswordIfChanged(
        User user,
        String newPassword,
        String ipAddress,
        String userAgent
    ) {
        if (!hasText(newPassword) || passwordEncoder.matches(newPassword, user.getPassword())) {
            return;
        }

        user.updatePassword(passwordEncoder.encode(newPassword));

        eventPublisher.publishEvent(new CredentialUpdatedEvent(
            user.getId(),
            user.getUsername(),
            ipAddress,
            userAgent
        ));
    }

    private String normalize(String value) {
        return value.strip().toLowerCase(Locale.ROOT);
    }

    private String normalizeIfPresent(String value) {
        return hasText(value) ? normalize(value) : null;
    }
}
