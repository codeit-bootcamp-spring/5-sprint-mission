package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.Getter;

@Getter
public class User extends BaseUpdatableEntity {
  private String username;
  private String email;
  private String password;
  private BinaryContent profile; // 0..1
  private UserStatus status;     // 1..1

  public User(String username, String email, String password, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public void update(String username, String email) {
    if (username != null) this.username = username;
    if (email != null) this.email = email;
  }

  public void changePassword(String newPassword) { this.password = newPassword; }
  public void changeProfile(BinaryContent newProfile) { this.profile = newProfile; }
  public void attachStatus(UserStatus status) { this.status = status; }
}
