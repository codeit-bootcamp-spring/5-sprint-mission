package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponseDto login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. " + loginRequest.username()));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 로그인 시 현재 시각으로 상태 갱신
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserStatus newStatus = new UserStatus(user.getId(), Instant.now());
                    userStatusRepository.save(newStatus);
                    return newStatus;
                });
        status.update(Instant.now()); // 로그인 시 lastActiveAt 갱신
        userStatusRepository.save(status);

        return UserResponseDto.fromEntity(user, status);
    }
}
