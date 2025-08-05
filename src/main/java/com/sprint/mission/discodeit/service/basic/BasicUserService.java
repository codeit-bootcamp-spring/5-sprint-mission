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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto.DetailResponse create(UserDto.CreateRequest request) {

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

        return UserDto.DetailResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .profileId(user.getProfileId())
            .isOnline(status.isOnline())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    @Override
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

    @Override
    public List<UserDto.DetailResponse> findAll() {
        List<User> users = userRepository.findAll();
        List<UserStatus> status = userStatusRepository.findAll();

        return users.stream().map(u -> {
            UserStatus s = status.stream().filter(t -> t.getUserId().equals(u.getId()))
                .findFirst().orElse(null);

            return UserDto.DetailResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .profileId(u.getProfileId())
                .isOnline(s != null && s.isOnline())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();

        }).collect(Collectors.toList());
    }

    @Override
    public UserDto.DetailResponse update(UserDto.UpdateRequest request) {
        User user = userRepository.findById(request.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        UUID newProfileId = user.getProfileId();
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            BinaryContent newProfile = BinaryContent.of(request.getProfileImage());
            binaryContentRepository.save(newProfile);
            newProfileId = newProfile.getId();
        }

        user.update(request, newProfileId);
        userRepository.save(user);

        UserStatus status = userStatusRepository.findByUserId(request.getId())
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

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        userStatusRepository.delete(userId);
        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        userRepository.delete(userId);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
