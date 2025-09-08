package com.sprint.mission.discodeit.controller; // 컨트롤러 패키지 선언

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor                                   // 생성자 주입(Lombok)
@RestController                                            // REST 컨트롤러
@RequestMapping("/api/auth")                               // 기본 URL 매핑
public class AuthController implements AuthApi {                              // 클래스 시작 (인터페이스 구현 제거)

    private final AuthService authService;                 // 인증 서비스 의존성
    private final UserMapper userMapper;                   // User 엔티티 → UserDto 매퍼 의존성

    @PostMapping(path = "/login")                          // 로그인 엔드포인트(기존 라우트/이름 유지)
    public ResponseEntity<UserDto> login(                  // 반환 타입을 UserDto로 리팩토링
                                                           @Valid @RequestBody LoginRequest loginRequest  // 요청 본문 검증 + 바인딩
    ) {
        User user = authService.login(loginRequest);       // 서비스에서 인증 처리(엔티티 반환)
        UserDto body = userMapper.toDto(user);             // 엔티티를 DTO로 매핑
        return ResponseEntity                              // 응답 빌더 시작
                .status(HttpStatus.OK)                     // 200 OK
                .body(body);                               // DTO 본문 반환
    }
}
