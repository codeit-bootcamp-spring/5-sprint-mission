package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User extends BaseUpdatableEntity {

  private String username;
  private String email;
  private String password;
  private UUID profileId;

  public void update(String username, String email, String password, UUID profileId) {
    if (checkUpdated(username, email, password, profileId)) {
      super.setUpdatedAt(Instant.now());
    }
  }

  private boolean checkUpdated(String username, String email, String password, UUID profileId) {
    boolean anyValueUpdated = false;

    if (username != null && !username.equals(this.username)) {
      this.username = username;
      anyValueUpdated = true;
    }
    if (email != null && !email.equals(this.email)) {
      this.email = email;
      anyValueUpdated = true;
    }
    if (password != null && !password.equals(this.password)) {
      this.password = password;
      anyValueUpdated = true;
    }
    if (profileId != null && !profileId.equals(this.profileId)) {
      this.profileId = profileId;
      anyValueUpdated = true;
    }

    return anyValueUpdated;
  }

}
