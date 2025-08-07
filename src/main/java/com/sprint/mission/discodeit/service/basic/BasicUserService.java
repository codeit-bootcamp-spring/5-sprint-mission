package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
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
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("BasicUserService")
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        for (User existing : userRepository.findAll()) {
            if (existing.getUsername().equals(request.getUsername()) ||
                    existing.getEmail().equals(request.getEmail())) {
                throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
            }
        }

        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());
        userRepository.save(user);

        if (request.hasProfileImage()) {
            BinaryContent profile = new BinaryContent(
                    "filename.jpg",
                    "image/jpeg",
                    (long) request.getNewProfileImage().length,  // 사이즈
                    request.getNewProfileImage()          // byte[]
            );
            binaryContentRepository.save(profile);
        }

        UserStatus status = new UserStatus(
                UUID.randomUUID(),
                user.getId(),
                Instant.now(),
                Instant.now()
        );
        userStatusRepository.save(status);

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), true);
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        boolean online = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), online);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean online = userStatusRepository.findByUserId(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(false);
                    return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), online);
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean update(UserUpdateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + request.getUserId() + " not found"));

        for (User existing : userRepository.findAll()) {
            if (!existing.getId().equals(user.getId())) {
                if (existing.getUsername().equals(request.getNewUsername()) ||
                        existing.getEmail().equals(request.getNewEmail())) {
                    throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
                }
            }
        }

        user.update(request.getNewUsername(), request.getNewEmail(), request.getNewPassword()); // ← 인자 개수 확인
        userRepository.save(user);

        if (request.hasNewProfileImage()) {
            byte[] imageBytes = request.getNewProfileImage();

            BinaryContent profile = new BinaryContent(
                    "profile.jpg",                // 파일 이름 (예시)
                    "image/jpeg",                 // 콘텐츠 타입 (예시)
                    (long) imageBytes.length,     // 크기
                    imageBytes                    // 바이트 데이터
            );

            binaryContentRepository.save(profile);
        }

        return true;
    }

        // DELETE
    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        userRepository.deleteById(userId);
        userStatusRepository.deleteByUserId(userId);
        binaryContentRepository.deleteByUserId(userId); // 구현 필요
    }
}
