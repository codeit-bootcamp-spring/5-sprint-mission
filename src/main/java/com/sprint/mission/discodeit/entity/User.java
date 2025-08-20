package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class User extends BaseEntity {

  private String name;
  private String email;
  private String password;
  private UUID profileId;

  public User(String name, String email, String password, UUID profileId) {
    super();
    this.name = name;
    this.email = email;
    this.password = password;
    this.profileId = profileId;
  }

  public void update(String name, UUID profileId) {
    this.name = name;
    this.profileId = profileId;
  }


  public void update(UpdateCommand update, UUID profileId) {
    boolean anyValueUpdated = false;

    if (update.getUsername() != null && !update.getUsername()
                                               .equals(this.name)) {
      this.name = update.getUsername();
      anyValueUpdated = true;
    }
    if (update.getEmail() != null && !update.getEmail()
                                            .equals(this.email)) {
      this.email = update.getEmail();
      anyValueUpdated = true;
    }
    if (update.getPassword() != null && !update.getPassword()
                                               .equals(this.password)) {
      this.password = update.getPassword();
      anyValueUpdated = true;
    }
    if (profileId != null && !profileId.equals(this.profileId)) {
      this.profileId = profileId;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      updateTimestamp();
    }
  }


  public static User of(CreateCommand request, UUID profileId) {
    return new User(request.getUsername(), request.getEmail(), request.getPassword(), profileId);
  }
}
