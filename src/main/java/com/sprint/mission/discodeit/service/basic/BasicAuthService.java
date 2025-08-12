package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto.DetailResponse login(String username, String password) {

        User user = userRepository.findByName(username).orElse(null);

        if (user == null || user.getPassword() == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("로그인 실패");
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
            .orElse(null);
        if(userStatus == null) {
            throw new IllegalArgumentException("비정상 유저");
        }

        userStatus.update();
        userStatusRepository.save(userStatus);

        return UserDto.DetailResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .profileId(user.getProfileId())
            .isOnline(userStatus.isOnline())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
