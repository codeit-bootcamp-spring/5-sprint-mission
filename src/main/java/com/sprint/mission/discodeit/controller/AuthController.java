package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/login"
    )
    public ResponseEntity<User> login(@RequestBody LoginRequest req) {
        if (req == null || req.username() == null || req.password() == null) {
            throw new IllegalArgumentException("Username or password is null");
        }
        return ResponseEntity.ok(authService.login(req));
    }
}
