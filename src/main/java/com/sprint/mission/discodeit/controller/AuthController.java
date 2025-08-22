package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.main.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sprint.mission.discodeit.dto.auth.request.LoginRequest;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(path = "login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
