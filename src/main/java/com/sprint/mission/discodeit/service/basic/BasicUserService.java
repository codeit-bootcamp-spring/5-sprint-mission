package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponseDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        BinaryContent profile = optionalProfileCreateRequest
            .map(profileRequest -> {
                BinaryContent binaryContent = new BinaryContent(
                    profileRequest.fileName(),
                    (long) profileRequest.bytes().length,
                    profileRequest.contentType()
                );
                return binaryContentRepository.save(binaryContent);
            })
            .orElse(null);

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setProfile(profile);

        User savedUser = userRepository.save(user);

        UserStatus status = new UserStatus();
        status.setUser(savedUser);
        status.setLastActiveAt(Instant.now());
        userStatusRepository.save(status);

        return userMapper.toDto(savedUser, status);
    }


    @Transactional(readOnly = true)
    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus status = userStatusRepository.findByUserId(userId).orElse(null);

        return userMapper.toDto(user, status);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
            .map(user -> {
                UserStatus status = userStatusRepository.findByUserId(user.getId()).orElse(null);
                return userMapper.toDto(user, status);
            })
            .toList();
    }

    @Transactional
    @Override
    public UserResponseDto update(UUID userId, UserUpdateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (userRepository.existsByEmail(request.email()) && !request.email().equals(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.email() + " already exists");
        }
        if (userRepository.existsByUsername(request.username()) && !request.username().equals(user.getUsername())) {
            throw new IllegalArgumentException("User with username " + request.username() + " already exists");
        }

        BinaryContent newProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                // 기존 프로필 삭제
                Optional.ofNullable(user.getProfile())
                    .ifPresent(binaryContentRepository::delete);

                BinaryContent binaryContent = new BinaryContent(
                    profileRequest.fileName(),
                    (long) profileRequest.bytes().length,
                    profileRequest.contentType()
                );
                return binaryContentRepository.save(binaryContent);
            })
            .orElse(user.getProfile());

        user.update(request.username(), request.email(), request.password(), newProfile);

        UserStatus status = userStatusRepository.findByUserId(userId).orElse(null);

        return userMapper.toDto(user, status);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus status = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("UserStatus not found for userId: " + userId));

        userRepository.delete(user);
        userStatusRepository.delete(status);
    }
}