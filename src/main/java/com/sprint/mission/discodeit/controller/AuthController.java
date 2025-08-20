package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.main.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.sprint.mission.discodeit.dto.auth.request.LoginRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest);
        return ResponseEntity.ok(user);
    }
}
