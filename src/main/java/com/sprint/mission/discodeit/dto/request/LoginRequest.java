package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

  @NotBlank(message = "유저 이름을 입력하세요.")
  private String username;

  @Size(min = 8, message = "8글자 이상만 입력하세요")
  private String password;
}
