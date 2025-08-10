package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.service.dto.user.ProfileImageUpload;
import com.sprint.mission.discodeit.service.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.service.dto.user.UserView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    /** 인메모리 매핑: userId -> profile(BinaryContent)Id */
    private final Map<UUID, UUID> userProfileMap = new ConcurrentHashMap<>();

    @Override
    public UserView create(CreateUserRequest request) {
        Objects.requireNonNull(request, "request");
        requireText(request.username, "username");
        requireText(request.email, "email");
        requireText(request.password, "password");

        // 유니크 검사 (레포 확장 없이 findAll 스캔)
        ensureUniqueUsernameAndEmail(request.username, request.email);

        // 유저 저장
        User saved = userRepository.save(new User(request.username, request.email, request.password));

        // 상태 생성
        UserStatus status = userStatusRepository.save(UserStatus.create(saved.getId(), Instant.now()));

        // 프로필(선택)
        request.profileImage.ifPresent(p -> {
            UUID pid = saveProfile(p).getId();
            userProfileMap.put(saved.getId(), pid);
        });

        return toView(saved, status);
    }


    @Override
    public UserView find(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus status = userStatusRepository.findByUserId(user.getId()).orElse(null);

        return toView(user, status);
    }

    @Override
    public List<UserView> findAll() {
        List<User> users = userRepository.findAll();

        // 각 사용자별로 상태를 개별 조회(인터페이스에 findAll 없음)
        return users.stream()
                .map(u -> toView(u, userStatusRepository.findByUserId(u.getId()).orElse(null)))
                .collect(Collectors.toList());
    }


    @Override
    public UserView update(UpdateUserRequest request) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(request.userId, "userId");

        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + request.userId + " not found"));

        // 변경 요청된 값에 한해 유니크 재검사
        if (hasText(request.newUsername) && !request.newUsername.equals(user.getUsername())) {
            ensureUniqueUsername(request.newUsername, user.getId());
        }
        if (hasText(request.newEmail) && !request.newEmail.equals(user.getEmail())) {
            ensureUniqueEmail(request.newEmail, user.getId());
        }

        // 엔티티 도메인 메서드로 수정(null은 미변경 처리 가정)
        user.update(request.newUsername, request.newEmail, request.newPassword);
        user = userRepository.save(user);

        // 프로필 제거
        if (request.removeProfileImage) {
            UUID old = userProfileMap.remove(user.getId());
            if (old != null && binaryContentRepository.existsById(old)) {
                binaryContentRepository.deleteById(old);
            }
        }

        // 프로필 교체
        request.newProfileImage.ifPresent(p -> {
            UUID old = userProfileMap.remove(user.getId());
            if (old != null && binaryContentRepository.existsById(old)) {
                binaryContentRepository.deleteById(old);
            }
            UUID pid = saveProfile(p).getId();
            userProfileMap.put(user.getId(), pid);
        });

        UserStatus status = userStatusRepository.findByUserId(user.getId()).orElse(null);
        return toView(user, status);
    }

    @Override
    public void delete(UUID userId) {
        Objects.requireNonNull(userId, "userId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // 프로필 먼저 삭제
        UUID pid = userProfileMap.remove(user.getId());
        if (pid != null && binaryContentRepository.existsById(pid)) {
            binaryContentRepository.deleteById(pid);
        }

        // 상태 삭제
        userStatusRepository.findByUserId(user.getId())
                .ifPresent(s -> userStatusRepository.deleteById(s.getId()));

        // 유저 삭제
        userRepository.deleteById(user.getId());
    }

    private void ensureUniqueUsernameAndEmail(String username, String email) {
        boolean usernameTaken = userRepository.findAll().stream()
                .anyMatch(u -> username.equals(u.getUsername()));
        if (usernameTaken) throw new IllegalArgumentException("username already exists");

        boolean emailTaken = userRepository.findAll().stream()
                .anyMatch(u -> email.equals(u.getEmail()));
        if (emailTaken) throw new IllegalArgumentException("email already exists");
    }

    private void ensureUniqueUsername(String newUsername, UUID selfId) {
        boolean taken = userRepository.findAll().stream()
                .anyMatch(u -> !u.getId().equals(selfId) && newUsername.equals(u.getUsername()));
        if (taken) throw new IllegalArgumentException("username already exists");
    }

    private void ensureUniqueEmail(String newEmail, UUID selfId) {
        boolean taken = userRepository.findAll().stream()
                .anyMatch(u -> !u.getId().equals(selfId) && newEmail.equals(u.getEmail()));
        if (taken) throw new IllegalArgumentException("email already exists");
    }

    private BinaryContent saveProfile(ProfileImageUpload p) {
        BinaryContent content = (p.data != null)
                ? BinaryContent.of(p.contentType, p.originalName, p.size, p.storageKey, p.data)
                : BinaryContent.reference(p.contentType, p.originalName, p.size, p.storageKey);
        return binaryContentRepository.save(content);
    }

    private UserView toView(User user, UserStatus status) {
        UUID profileId = userProfileMap.get(user.getId()); // Map 기반
        boolean online = status != null && status.isOnline();
        Instant lastSeen = status != null ? status.getLastSeenAt() : null;

        return new UserView(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profileId,
                online,
                lastSeen,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private void requireText(String v, String name) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(name + " is required");
    }
    private boolean hasText(String v) { return v != null && !v.isBlank(); }
}
