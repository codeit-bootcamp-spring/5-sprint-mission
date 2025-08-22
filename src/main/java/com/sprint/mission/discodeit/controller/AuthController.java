package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.LoginDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth API")
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserStatusService userStatusService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse.detail> login(@Valid @RequestBody LoginDto.request req) {
        User user = authService.login(req.username(), req.password());
        boolean online = userStatusService.isOnline(user.getId());

        return ResponseEntity.ok(UserResponse.detail.from(user, online));
    }
}
