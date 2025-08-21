package com.sprint.mission.discodeit.controller.restController;


import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.LoginResponse;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class AuthController {

  private final AuthService authService;

  @RequestMapping(method = RequestMethod.GET)
  public LoginResponse login(
      @RequestBody LoginRequest loginRequest
  ) {
    return authService.login(loginRequest);
  }


}
