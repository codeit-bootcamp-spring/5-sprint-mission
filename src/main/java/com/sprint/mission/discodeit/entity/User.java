package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;   // 중요: jakarta 패키지
import lombok.Getter;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email")
    }
)
@Getter
public class User extends BaseUpdatableEntity {

  @Column(name = "username", length = 50, nullable = false, unique = true)
  private String username;

  @Column(name = "email", length = 100, nullable = false, unique = true)
  private String email;

  @Column(name = "password", length = 60, nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  protected User() {}

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
}

