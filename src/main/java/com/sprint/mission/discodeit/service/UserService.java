package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("userService")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    public User create(UserCreateRequest request) {
        validateUnique(request.username(), request.email());
        User user;

        if (request.uploadProfileImage()) {
            user = new User(request.username(), request.email(), request.password(), request.profileId());
        } else {
            user = new User(request.username(), request.email(), request.password());
        }
        userStatusRepository.save(new UserStatus(user.getId()));
        return userRepository.save(user);
    }

    public UserFindResponse findById(UUID userId) {
        User user = null;

        user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        return UserFindResponse.builder()
                .profileId(user.getProfileId())
                .username(user.getUsername())
                .email(user.getEmail())
                .loginStatus(userStatusRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new NoSuchElementException("Not found")).isLogin())
                .build();
    }

    public List<UserFindResponse> findAll() {
        List<UserFindResponse> userFindResponses = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new NoSuchElementException("UserStatus Not found"));
            userFindResponses.add(UserFindResponse.builder()
                    .profileId(user.getProfileId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .loginStatus(userStatus.isLogin())
                    .build());
        }
        return userFindResponses;
    }

    public User update(UserUpdateRequest request) {
        validateUnique(request.username(), request.email());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + request.userId() + " not found"));

        if (request.updateProfileImage()) {
            user.update(request.username(), request.email(), request.password(), request.profileId());
        } else {
            user.update(request.username(), request.email(), request.password());
        }
        return userRepository.save(user);
    }

    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userStatusRepository.findByUserId(user.getId()).ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

        userRepository.deleteById(user.getId());
    }

    public void deleteAll() {
        userRepository.findAll().forEach(user -> delete(user.getId()));
    }

    private void validateUnique(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username " + username + " already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email " + email + " already exists");
        }
    }
}
