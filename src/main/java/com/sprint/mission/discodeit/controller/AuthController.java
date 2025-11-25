package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor                                   // 생성자 주입(Lombok)
@RestController                                            // REST 컨트롤러
@RequestMapping("/api/auth")                               // 기본 URL 매핑
public class AuthController implements AuthApi {                              // 클래스 시작 (인터페이스 구현 제거)

    private final AuthService authService;                 // 인증 서비스 의존성

    @PostMapping(path = "/login")                          // 로그인 엔드포인트(기존 라우트/이름 유지)
    public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 요청: username={}", loginRequest.username());
        UserDto user = authService.login(loginRequest);       // 서비스에서 인증 처리(엔티티 반환)
        log.debug("로그인 응답: {}", user);
        return ResponseEntity                              // 응답 빌더 시작
                .status(HttpStatus.OK)                     // 200 OK
                .body(user);                               // DTO 본문 반환
    }
}
