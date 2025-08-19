package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.request.LoginRequest;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        if (!StringUtils.hasText(loginRequest.username())) {
            throw new IllegalArgumentException("username이 필요합니다.");
        }
        if (!StringUtils.hasText(loginRequest.password())) {
            throw new IllegalArgumentException("password가 필요합니다.");
        }

        User loginedUser = authService.login(loginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(loginedUser);
    }
}
