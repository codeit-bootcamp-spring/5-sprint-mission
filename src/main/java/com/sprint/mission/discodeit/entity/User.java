package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserDto;
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


  public void update(UserDto.UpdateRequest request, UUID profileId) {
    boolean anyValueUpdated = false;

    if (request.getUsername() != null && !request.getUsername().equals(this.name)) {
      this.name = request.getUsername();
      anyValueUpdated = true;
    }
    if (request.getEmail() != null && !request.getEmail().equals(this.email)) {
      this.email = request.getEmail();
      anyValueUpdated = true;
    }
    if (request.getPassword() != null && !request.getPassword().equals(this.password)) {
      this.password = request.getPassword();
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


  public static User of(UserDto.CreateRequest request, UUID profileId) {
    return new User(
        request.getUsername(),
        request.getEmail(),
        request.getPassword(),
        profileId
    );
  }
}
