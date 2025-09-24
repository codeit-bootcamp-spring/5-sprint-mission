package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.controller.api.AuthApi;
import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.LoginRequest;
import com.codeit.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;

    @PostMapping(path = "login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("로그인 API 호출 - username: {}", loginRequest.username());
        UserDto user = authService.login(loginRequest);
        log.info("로그인 API 성공 - username: {}, userId: {}", loginRequest.username(), user.id());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
