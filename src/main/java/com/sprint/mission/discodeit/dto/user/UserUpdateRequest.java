package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserUpdateRequest {

  private String newUsername;
  private String newEmail;
  private String newPassword;


  public UserUpdateRequest(String newUsername, String newEmail, String newPassword) {
    this.newUsername = newUsername;
    this.newEmail = newEmail;
    this.newPassword = newPassword;
  }

}
