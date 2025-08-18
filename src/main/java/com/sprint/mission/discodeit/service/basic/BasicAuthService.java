package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusService userStatusService;

    @Override
    public LoginDto.response login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 비밀번호 일치 여부 확인
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userStatusService.updateLastAccessedAt(user.getId()); // 접속 시각 갱신
        boolean isOnline = userStatusService.isOnline(user.getId());
        String imageUrl = (user.getProfileId() != null) ? "/binary/" + user.getProfileId() : null;

        return LoginDto.response.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .isOnline(isOnline)
                .imageUrl(imageUrl)
                .build();
    }
}
