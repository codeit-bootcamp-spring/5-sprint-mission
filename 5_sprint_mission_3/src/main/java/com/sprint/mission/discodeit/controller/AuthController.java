package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        try {
            UserResponseDto userResponse = authService.login(userLoginRequest);
            return ResponseEntity.ok(userResponse);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(401).body(e.getMessage()); // 로그인 실패 시 401 에러 반환
        }
    }
}