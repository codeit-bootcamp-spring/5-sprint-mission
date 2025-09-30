package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class AuthDto {

  @Getter
  @Schema(name = "LoginRequest")
  public static class LoginRequest {

    @NotBlank(message = "사용자명은 필수 입력값입니다.")
    private String username;
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
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
