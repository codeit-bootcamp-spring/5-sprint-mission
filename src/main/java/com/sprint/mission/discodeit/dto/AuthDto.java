package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class AuthDto {

  @Getter
  @Schema(name = "LoginRequest")
  public static class LoginRequest {

    private String username;
    private String password;

    public Login toLogin() {
      return Login.builder()
                  .username(this.username)
                  .password(this.password)
                  .build();
    }
  }

  @Getter
  @Builder
  public static class Login {

    private String username;
    private String password;
  }
}
