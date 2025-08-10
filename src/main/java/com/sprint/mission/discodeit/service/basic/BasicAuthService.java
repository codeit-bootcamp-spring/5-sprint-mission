package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.service.dto.auth.LoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService { // 인증 서비스 구현 시작

    private final UserRepository userRepository; // 같은 레이어의 다른 Service가 아닌 Repository만 의존

    @Override
    public LoginResult login(LoginRequest request) {
        // 1) 방어적 검증: 파라미터 유효성
        if (request == null || isBlank(request.username) || isBlank(request.password)) {
            throw new IllegalArgumentException("username과 password는 필수입니다.");
        }

        // 2) 레포지토리 변경 없이 findAll 스캔으로 사용자 탐색
        //    (나중에 최적화가 필요하면 exists/findByUsername 같은 메서드를 레포에 '추가'하면 됨)
        User matched = userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getUsername(), request.username)
                        && Objects.equals(u.getPassword(), request.password)) // 현재는 단순 문자열 비교
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("username 또는 password가 올바르지 않습니다."));

        // 3) 비밀번호는 절대 노출하지 않는 결과 DTO로 변환
        return new LoginResult(
                matched.getId(),
                matched.getUsername(),
                matched.getEmail()
        );

    }

    // 공백/널 체크 유틸
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
