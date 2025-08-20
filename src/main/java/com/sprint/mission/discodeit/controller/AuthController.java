package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

       /* Controller : 요청 자체가 null인지 확인
           Service : 요청 내용이 올바른지 판단 */

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<User> login(@RequestBody(required = false) LoginRequest request) {

        //값 자체가 NULL인지 확인
        if (request == null) {
            return ResponseEntity.badRequest().body(null); // or .build();
        }

        //서비스 호출
        User user = authService.login(request);
        return ResponseEntity.ok(user);
    }
}
