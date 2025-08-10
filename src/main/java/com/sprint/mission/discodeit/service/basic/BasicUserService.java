package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
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
import java.util.UUID;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("아이디 또는 이메일이 잘못되었습니다.");
        }

        UUID profileImageId = request.getProfileImageId();
        if (profileImageId != null || !binaryContentRepository.existsById(profileImageId)) {
            throw new RuntimeException("등록되지 않은 프로필 이미지 입니다.");
        }

        User user = new User(request.getUsername(), request.getEmail(), request.getPassword(), profileImageId);
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(userStatus);

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), true);
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), isOnline);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean isOnline = userStatusRepository.findByUserId(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(false);
                    return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), isOnline);
                })
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        UUID newProfileImageId = request.getProfileImageId();
        if (newProfileImageId != null || !binaryContentRepository.existsById(newProfileImageId)) {
            throw new RuntimeException("프로필 이미지를 찾을 수 없습니다.");
        }

        user.update(request.getUsername(), request.getEmail(), request.getPassword(), newProfileImageId);
        userRepository.save(user);

        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), isOnline);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId());
        }
        userStatusRepository.findByUserId(user.getId())
                        .ifPresent(status -> userStatusRepository.deleteById(status.getId()));

        userRepository.deleteById(user.getId());
    }
}
