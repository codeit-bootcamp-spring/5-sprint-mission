package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.request.AuthLogoutRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/login")
    public ResponseEntity<UserResponse> login(@RequestBody AuthLoginRequest body) {
        return ResponseEntity.ok(authService.login(body));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody AuthLogoutRequest body) {
        authService.logout(body.userId());
        return ResponseEntity.ok().build();
    }
}
