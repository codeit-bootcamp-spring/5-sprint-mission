package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.user.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(path = "login", method = RequestMethod.POST)
    public ResponseEntity<User> login(@RequestBody UserLoginRequest request) {
        User loginUser = authService.login(request);
        return ResponseEntity.ok(loginUser);
    }
}
