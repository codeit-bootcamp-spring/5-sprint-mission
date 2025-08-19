package com.sprint.mission.discodeit.dto;

import lombok.Getter;

public class AuthDto {

  @Getter
  public static class LoginRequest {

    private String username;
    private String password;
  }
}
