package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;

public class AuthDto {

  @Getter
  public static class Request {

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
