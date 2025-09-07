package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, length = 50)
  private String username;

  @Column(nullable = true, length = 100)
  private String email;

  @Column(nullable = true)
  private String password;

  @OneToOne
  @JoinColumn(name = "profile_id")
  @Setter
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Setter
  private UserStatus status;

  @Column(nullable = false)
  @Setter
  private boolean online;

  public User(String username, String email, String password, BinaryContent profile, boolean online) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.online = online;
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
      updateTimestamp(Instant.now());
    }
  }
}
