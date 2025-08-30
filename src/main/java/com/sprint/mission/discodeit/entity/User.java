package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseUpdatableEntity {

  private String username;
  private String email;
  private String password;

  @OneToOne
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  public User(String username, String email, String password, BinaryContent profile) {
    super();
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public void attachStatus(UserStatus status) {
    this.status = status;
    status.linkToUser(this);
  }

  public void update(String username, String email, String password, BinaryContent profile) {
    if (checkUpdated(username, email, password, profile)) {
      super.setUpdatedAt(Instant.now());
    }
  }

  private boolean checkUpdated(String username, String email, String password,
      BinaryContent profile) {
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
    if (profile != null && !profile.equals(this.profile)) {
      this.profile = profile;
      anyValueUpdated = true;
    }

    return anyValueUpdated;
  }

}
