package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
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
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("BasicUserService")
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID create(UserCreateRequest request) {
        // 중복 방지
        boolean duplicated = userRepository.findAll().stream().anyMatch(u ->
                u.getUsername().equals(request.getUsername()) ||
                        u.getEmail().equals(request.getEmail()));
        if (duplicated) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());
        userRepository.save(user);

        if (request .hasProfileImage()) {
            byte[] bytes = request.getNewProfileImage();
            binaryContentRepository.save(new BinaryContent(
                    "profile.jpg", "image/jpeg", (long) bytes.length, bytes
            ));
        }

        userStatusRepository.save(new UserStatus(user.getId(), Instant.now()));

        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline).orElse(false);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), online).getId();
    }

    @Override
    public Optional<UserResponse> read(UUID id) {
        return userRepository.findById(id).map(u -> {
            boolean online = userStatusRepository.findByUserId(u.getId())
                    .map(UserStatus::isOnline).orElse(false);
            return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), online);
        });
    }

    @Override
    public List<UserResponse> readAll() {
        return userRepository.findAll().stream()
                .map(u -> {
                    boolean online = userStatusRepository.findByUserId(u.getId())
                            .map(UserStatus::isOnline).orElse(false);
                    return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), online);
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean update(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + request.getId() + " not found"));

        // 중복 체크는 유지
        for (User existing : userRepository.findAll()) {
            if (!existing.getId().equals(user.getId())) {
                if (Objects.equals(existing.getUsername(), request.getNewUsername()) ||
                        Objects.equals(existing.getEmail(), request.getNewEmail())) {
                    throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
                }
            }
        }
        user.update(request.getNewUsername(), request.getNewEmail(), request.getNewPassword());
        userRepository.save(user);

        return true;
    }

    @Override
    public boolean delete(UUID userId) {
        if (!userRepository.existsById(userId)) return false;

        userRepository.deleteById(userId);
        userStatusRepository.deleteByUserId(userId);
        binaryContentRepository.deleteByUserId(userId);

        return !userRepository.existsById(userId);
    }
}

