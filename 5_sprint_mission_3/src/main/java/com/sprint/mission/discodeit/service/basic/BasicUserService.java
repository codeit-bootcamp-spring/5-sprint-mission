package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository; // 구현체는 이후에 작성

    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        return userRepository.save(user);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        boolean online = user.isOnline();

        return new UserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                online
        );
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getProfileImageId(),
                        user.isOnline()
                ))
                .toList();
    }

    @Override
    public UserResponseDto update(UserUpdateRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new IllegalArgumentException("이미 존재하는 username입니다.");
            }
        }
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("이미 존재하는 email입니다.");
            }
        }

        if (request.profileImage() != null) {
            BinaryContent binaryContent = new BinaryContent(
                    request.profileImage().filename(),
                    request.profileImage().contentType(),
                    request.profileImage().size(),
                    request.profileImage().data()
            );
            binaryContent = binaryContentRepository.save(binaryContent);
            user.setProfileImageId(binaryContent.getId());
        }
        user.update(request.username(), request.email(), request.password());
        userRepository.save(user);

        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                online
        );
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));
        UUID profileImageId = user.getProfileImageId();
        if (profileImageId != null) {
            binaryContentRepository.deleteById(profileImageId);
        }
        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.deleteById(status.getId()));
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponseDto create(UserCreateRequest request, BinaryContentCreateRequest profileImage) {
        User user = new User(request.username(), request.email(), request.password());
        user = userRepository.save(user);

        UserStatus status = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(status);

        return new UserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                status.isOnline()
        );
    }

    @Override
    public void updateLastAccessAt(UUID userId, Instant lastAccessAt) {
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElse(new UserStatus(userId, lastAccessAt));

        status.userAccessUpdate(lastAccessAt);
        userStatusRepository.save(status);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setLastAccessAt(lastAccessAt);
        userRepository.save(user);
    }
}
