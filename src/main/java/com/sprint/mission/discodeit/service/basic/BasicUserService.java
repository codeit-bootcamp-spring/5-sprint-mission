package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
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
    private final UserStatusRepository userStatusRepository;
    private final FileBinaryContentRepository fileBinaryContentRepository;

    @Override
    public UserResponseDto create(UserCreateRequest request) {
        if (userRepository.existsByName(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User(request.username(), request.email(), request.password());

        if (request.profileImage() != null) {
            if (!fileBinaryContentRepository.existsById(request.profileImage())) {
                throw new IllegalArgumentException("Profile image does not exist");
            }
            user.setProfileId(request.profileImage());
        }

        User savedUser = userRepository.save(user);
        UserStatus status = new UserStatus(UUID.randomUUID(), savedUser.getId());
        userStatusRepository.save(status);

        return UserResponseDto.fromEntity(savedUser, status);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for userId: " + userId));
        return UserResponseDto.fromEntity(user, status);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();

        List<UserResponseDto> userResponseList = new ArrayList<>();
        for (User user : users) {
            // 누락된 UserStatus 자동 복구(백필)
            UserStatus status = userStatusRepository.findByUserId(user.getId())
                    .orElseGet(() -> userStatusRepository.save(new UserStatus(UUID.randomUUID(), user.getId())));

            UserResponseDto userResponseDto = UserResponseDto.fromEntity(user, status);
            userResponseList.add(userResponseDto);
        }
        return userResponseList;
    }

    @Override
    public UserResponseDto update(UserUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + request.userId() + " not found"));
        user.update(request.username(), request.email(), request.password());
        User savedUser = userRepository.save(user);

        UserStatus status = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for userId: " + request.userId()));
        status.update();
        userStatusRepository.save(status);

        return UserResponseDto.fromEntity(savedUser, status);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for userId: " + userId));

        userRepository.deleteById(userId);
        userStatusRepository.deleteById(status.getId());
    }
}