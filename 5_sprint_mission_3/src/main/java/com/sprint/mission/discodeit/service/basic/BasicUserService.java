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
@Primary
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

//    @Override
//    public User find(UUID userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
//    }
//
//    @Override
//    public List<User> findAll() {
//        return userRepository.findAll();
//    }


    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElse(null);

        boolean online = (userStatus != null) && userStatus.isOnline();

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                online
        );
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();

        List<UserResponseDto> result = new ArrayList<>();
        for (User user : users) {
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                    .orElse(null);

            boolean online = (userStatus != null) && userStatus.isOnline();

            result.add(new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileImageId(),
                    online
            ));
        }

        return result;
    }


//    @Override
//    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
//        user.update(newUsername, newEmail, newPassword);
//        return userRepository.save(user);
//    }

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
    public User create(UserCreateRequest request, @Nullable BinaryContentCreateRequest profileImage) {
        if (userRepository.existsByUsername(request.username())){
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }
        if (userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("이미 존재하는 email입니다.");
        }
        UUID profileImageId = null;
        if (profileImage != null) {
            BinaryContent binaryContent = new BinaryContent(
                    profileImage.filename(),
                    profileImage.contentType(),
                    profileImage.size(),
                    profileImage.data()
            );
            binaryContent = binaryContentRepository.save(binaryContent);
            profileImageId = binaryContent.getId();
        }
        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileImageId
        );
        user = userRepository.save(user);

        UserStatus userStatus = new UserStatus(
                user.getId(),
                Instant.now()
        );
        userStatusRepository.save(userStatus);
        return user;
    }
}
