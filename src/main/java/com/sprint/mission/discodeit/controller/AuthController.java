package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody UserLoginRequest userLoginRequest) {
        User user = authService.login(userLoginRequest);

        return user.getUsername() + "님 환영합니다.";
    }
}
