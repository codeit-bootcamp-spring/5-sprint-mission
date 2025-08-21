package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
public class UserCreateRequest {

  private String username;
  private String email;
  private String password;

}
