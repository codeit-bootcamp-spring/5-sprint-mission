package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest, BinaryContentCreateRequest binaryContentCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("This username is already taken: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("This email is already taken");
        }

        BinaryContent profile = getBinaryContentNullable(binaryContentCreateRequest);

        String password = userCreateRequest.password();

        User user = new User(username, email, password, profile, null);
        UserStatus userStatus = new UserStatus(user, Instant.now());

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto find(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto update(UUID id, UserUpdateRequest userUpdateRequest, BinaryContentCreateRequest binaryContentCreateRequest) {
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

        BinaryContent newProfile = getBinaryContentNullable(binaryContentCreateRequest);

        String newPassword = userUpdateRequest.newPassword();

        user.update(newUsername, newEmail, newPassword, newProfile);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found: " + id);
        }

        userRepository.deleteById(id);
    }

    private BinaryContent getBinaryContentNullable(BinaryContentCreateRequest binaryContentCreateRequest) {
        BinaryContent profile = null;
        if (binaryContentCreateRequest != null) {
            String fileName = binaryContentCreateRequest.fileName();
            String contentType = binaryContentCreateRequest.contentType();
            byte[] bytes = binaryContentCreateRequest.bytes();
            if (bytes.length > 0) {
                profile = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
            }
        }
        return profile;
    }
}
