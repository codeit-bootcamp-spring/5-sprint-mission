package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.CreateFile;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse register(CreateUserRequest userRequest, CreateFile profileImage) {
        if (!isUnique(userRequest.email(), userRequest.userName())) return null;

        UUID profileId = null;
        if (profileImage != null) {
            BinaryContent saved = binaryContentRepository.save(
                    new BinaryContent(profileImage.fileName(), profileImage.fileType(), profileImage.data(), profileImage.fileSize())
            );
            profileId = saved.getId();
        }

        User user = new User(profileId, userRequest.email(), userRequest.userName(), userRequest.nickname(), userRequest.password(), userRequest.phoneNumber());
        userRepository.save(user);

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return toResponse(user, status);
    }

    @Override
    public Optional<UserResponse> getById(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return Optional.empty();

        User user = optionalUser.get();
        UserStatus status = userStatusRepository.findByUserId(user.getId()).get();
        return Optional.of(toResponse(user, status));
    }

    @Override
    public Optional<UserResponse> getByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) return Optional.empty();

        User user = optionalUser.get();
        UserStatus status = userStatusRepository.findByUserId(user.getId()).get();
        return Optional.of(toResponse(user, status));
    }

    @Override
    public Optional<UserResponse> getByUserName(String userName) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        if (optionalUser.isEmpty()) return Optional.empty();

        User user = optionalUser.get();
        UserStatus status = userStatusRepository.findByUserId(user.getId()).get();
        return Optional.of(toResponse(user, status));
    }

    @Override
    public List<UserResponse> searchByNickname(String nickname) {
        return userRepository.findByNickName(nickname).stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId()).get();
                    return toResponse(user, status);
                })
                .toList();
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId()).get();
                    return toResponse(user, status);
                })
                .toList();
    }

    @Override
    public UserResponse update(UpdateUserRequest userRequest, CreateFile profileImage) {
        if (!isUnique(userRequest.email(), userRequest.userName())) return null;

        Optional<User> optionalUser = userRepository.findById(userRequest.userId());
        if (optionalUser.isEmpty()) return null;

        User user = optionalUser.get();
        user.update(userRequest);

        if (profileImage != null) {
            Optional.ofNullable(user.getProfileId()).ifPresent(binaryContentRepository::delete);
            BinaryContent saved = binaryContentRepository.save(
                    new BinaryContent(profileImage.fileName(), profileImage.fileType(), profileImage.data(), profileImage.fileSize())
            );
            user.changeProfileId(saved.getId());
        }
        userRepository.save(user);

        UserStatus status = userStatusRepository.findByUserId(user.getId()).get();
        return toResponse(user, status);
    }

    @Override
    public boolean remove(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) return false;

        User user = optionalUser.get();
        Optional.ofNullable(user.getProfileId()).ifPresent(binaryContentRepository::delete);
        userStatusRepository.delete(userId);
        return userRepository.delete(userId);
    }

    private UserResponse toResponse(User user, UserStatus status) {
        return new UserResponse(user.getId(), user.getProfileId(), user.getEmail(), user.getUserName(), user.getNickname(), user.getPhoneNumber(), status.isOnline(), status.getLastActiveAt());
    }

    private boolean isUnique(String email, String username) {
        return userRepository.findByEmail(email).isEmpty() && userRepository.findByUserName(username).isEmpty();
    }
}
