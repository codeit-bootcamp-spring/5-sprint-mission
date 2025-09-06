package com.sprint.mission.discodeit.dto;

import java.util.UUID;
import lombok.Data;


/* 요청,응답 dto 통합
 */

@Data
public class UserDto {

  // 공통 필드
  private UUID id;
  private String username;
  private String email;

  // Create/Update 요청용
  private String password;
  private String newUsername;
  private String newEmail;
  private String newPassword;

  // 응답용
  private UUID profileId;
  private Boolean online;

}

