package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserCreateRequest userCreateRequest, @Nullable BinaryContentCreateRequest binaryContentCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        User user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());

        if (binaryContentCreateRequest != null) {
            BinaryContent profile = new BinaryContent(binaryContentCreateRequest.fileName(), binaryContentCreateRequest.contentType(), binaryContentCreateRequest.binaryContent());
            user.updateProfile(profile.getId());
            binaryContentRepository.save(profile);
        }

        UserStatus userStatus = new UserStatus(user.getId());

        userRepository.save(user);
        userStatusRepository.save(userStatus);

        return user;
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream().map(this::toDto)
                .toList();
    }

    @Override
    public User update(UUID userId, UserUpdateRequest userUpdateRequest, @Nullable BinaryContentCreateRequest binaryContentCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        if (binaryContentCreateRequest != null) {
            BinaryContent profile = new BinaryContent(binaryContentCreateRequest.fileName(), binaryContentCreateRequest.contentType()
                    , binaryContentCreateRequest.binaryContent());
            user.updateProfile(profile.getId());
            binaryContentRepository.save(profile);
        }
        user.update(userUpdateRequest.newUsername(), userUpdateRequest.newEmail(), userUpdateRequest.newPassword());
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        userRepository.deleteById(userId);
        userStatusRepository.deleteByUserId(userId);

        UUID profileId = user.getProfiledId();
        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

    }

    private UserDto toDto(User user) {
        UserStatus userStatus = userStatusRepository.findById(user.getId()).orElse(null);
        Boolean online = userStatus.isLogin();

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfiledId(),
                online
        );
    }
}
