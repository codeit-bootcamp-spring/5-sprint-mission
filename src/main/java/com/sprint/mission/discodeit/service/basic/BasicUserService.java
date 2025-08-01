package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    public void create(UserDto.CreateRequest request) throws IOException {

        if (userRepository.existsByUsername(request.getName())) {
            throw new IllegalArgumentException("이미 사용 중인 username입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 email입니다.");
        }

        BinaryContent profile = null;
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            profile = BinaryContent.of(request.getProfileImage());
            binaryContentRepository.save(profile);
        }

        User user = User.of(request, profile != null ? profile.getId() : null);
        userRepository.save(user);

        UserStatus status = UserStatus.of(user.getId());
        userStatusRepository.save(status);
    }

    public UserDto.DetailResponse findById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserStatus status = userStatusRepository.findByUserId(userId)
            .orElse(null);

        return UserDto.DetailResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .profileId(user.getProfileId())
            .isOnline(status != null && status.isOnline())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    public void update(UserDto.UpdateRequest request) throws IOException {
        User user = userRepository.findById(request.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        UUID newProfileId = user.getProfileId();
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            BinaryContent newProfile = BinaryContent.of(request.getProfileImage());
            binaryContentRepository.save(newProfile);
            newProfileId = newProfile.getId();
        }

        user.update(request.getName(), newProfileId);
        userRepository.save(user);
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User create(String name, String email, String password) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public User get(UUID id) {
        return null;
    }

    @Override
    public User update(UUID id, String name, UUID profileId) {
        return null;
    }

    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        userStatusRepository.deleteByUserId(userId);
        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        userRepository.delete(userId);
    }

    @Override
    public void deleteAll() {

    }
}
