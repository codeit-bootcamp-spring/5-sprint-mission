package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateRequest userCreateRequest, BinaryContentCreateRequest binaryContentCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("This username is already taken: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("This email is already taken");
        }

        UUID profileId = null;
        if (binaryContentCreateRequest != null) {
            String fileName = binaryContentCreateRequest.fileName();
            String contentType = binaryContentCreateRequest.contentType();
            byte[] bytes = binaryContentCreateRequest.bytes();
            BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);

            profileId = binaryContentRepository.save(binaryContent).getId();
        }

        String nickname = userCreateRequest.nickname();
        String password = userCreateRequest.password();

        User user = new User(username, nickname, email, password, profileId);
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(userStatus);

        return user;
    }

    @Override
    public UserDto find(UUID id) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        return UserDto.from(user, isOnline(id));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserDto.from(user, isOnline(user.getId())))
                .toList();
    }

    @Override
    public User update(UUID id, UserUpdateRequest userUpdateRequest, BinaryContentCreateRequest binaryContentCreateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("This username is already taken");
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("This email is already taken");
        }

        UUID newProfileId = null;
        if (binaryContentCreateRequest != null) {
            Optional.ofNullable(user.getProfileId())
                    .ifPresent(binaryContentRepository::delete);

            String fileName = binaryContentCreateRequest.fileName();
            String contentType = binaryContentCreateRequest.contentType();
            byte[] bytes = binaryContentCreateRequest.bytes();
            BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);

            newProfileId = binaryContentRepository.save(binaryContent).getId();
        }

        String newNickname = userUpdateRequest.newNickname();
        String newPassword = userUpdateRequest.newPassword();

        user.update(newUsername, newNickname, newEmail, newPassword, newProfileId);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        Optional.ofNullable(user.getProfileId())
                        .ifPresent(binaryContentRepository::delete);

        userStatusRepository.deleteByUserId(user.getId());
        userRepository.delete(user.getId());
    }

    private Boolean isOnline(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .map(UserStatus::online)
                .orElse(null);
    }
}
