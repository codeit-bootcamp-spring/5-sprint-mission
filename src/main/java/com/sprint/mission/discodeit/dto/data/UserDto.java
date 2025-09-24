package com.sprint.mission.discodeit.dto.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Data;


/* 요청,응답 dto 통합
 */

@Data
public class UserDto {

  // 공통 필드
  private UUID id;

  @NotBlank(message = "이름은 필수입니다.")
  private String username;

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  // Create/Update 요청용
  @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
  private String password;

  private String newUsername;
  @Email(message = "새 이메일 형식이 올바르지 않습니다.")
  private String newEmail;

  @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
  private String newPassword;

  // 응답용
  private UUID profileId;
  private Boolean online;

}

