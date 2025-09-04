package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

//  private static final long serialVersionUID = 1L;
  //
  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  private String password;

  @OneToOne
  @JoinColumn(nullable = true)
  private BinaryContent profile;// BinaryContent

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = true)
  @JsonManagedReference
  private UserStatus status;

//  public User(String username, String email, String password, UUID profileId) {
//    this.username = username;
//    this.email = email;
//    this.password = password;
//    this.profileId = profileId;
//  }

  public User(String username, String email, String password, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }


  public User() {

  }

    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
    boolean anyValueUpdated = false;
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
      anyValueUpdated = true;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
      anyValueUpdated = true;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
      anyValueUpdated = true;
    }
    if (newProfile != null && !newProfile.equals(this.profile)) {
      this.profile = newProfile;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
