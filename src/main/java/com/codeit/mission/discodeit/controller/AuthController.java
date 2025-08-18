package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.LoginRequest;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("loginRequest가 필요합니다.");
        }
        if (loginRequest.username() == null || loginRequest.username().trim().isEmpty()) {
            throw new IllegalArgumentException("username이 필요합니다.");
        }
        if (loginRequest.password() == null || loginRequest.password().trim().isEmpty()) {
            throw new IllegalArgumentException("password가 필요합니다.");
        }

        User loginedUser = authService.login(loginRequest);

        UserDto userDto = new UserDto(
                loginedUser.getId(),
                loginedUser.getCreatedAt(),
                loginedUser.getUpdatedAt(),
                loginedUser.getUsername(),
                loginedUser.getEmail(),
                loginedUser.getProfileId(),
                true
        );

        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }
}
