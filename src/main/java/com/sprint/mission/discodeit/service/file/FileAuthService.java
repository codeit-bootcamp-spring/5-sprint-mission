package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        //1. null 체크
        if (loginRequest == null) {
            throw new IllegalArgumentException("로그인 요청이 null입니다.");
        }

        // 2. 모든 유저를 가져옴
        return userRepository.findAll().stream()// 하나하나 검사하기
                .filter(user -> user.getUserId().equals(loginRequest.getUserId())
                        && user.getPassword().equals(loginRequest.getPassword())) // 아이디&비번 둘다 맞는 사람 필터
                .findFirst() // 첫번째 유저 반환
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }
}
