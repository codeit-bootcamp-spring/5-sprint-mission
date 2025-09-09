package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;


import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uq_users_email", columnNames = "email")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  @Column(name = "username", nullable = false, length = 50)
  private String username;

  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @Column(name = "password", nullable = false, length = 60)
  private String password;

  /** 0..1 다대일(여러 유저가 같은 파일을 참조할 수도 있음) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id") // ON DELETE SET NULL (DB 스키마에 반영됨)
  private BinaryContent profile;

  /** 1:1 양방향 - FK는 UserStatus.user_id (주인: UserStatus) */
  @OneToOne(mappedBy = "user",
      cascade = CascadeType.ALL,      //생성/수정/삭제 전부 전이
      orphanRemoval = true,           //User에서 떼면 UserStatus 삭제
      fetch = FetchType.LAZY)
  private UserStatus status;

  public User(String username, String email, String password, BinaryContent profile) {
    if (username == null || username.isBlank()) throw new IllegalArgumentException("username is required");
    if (email == null || email.isBlank()) throw new IllegalArgumentException("email is required");
    if (password == null || password.isBlank()) throw new IllegalArgumentException("password is required");
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public boolean update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
    boolean changed = false;
    if (newUsername != null && !newUsername.equals(this.username)) { this.username = newUsername; changed = true; }
    if (newEmail != null && !newEmail.equals(this.email)) { this.email = newEmail; changed = true; }
    if (newPassword != null && !newPassword.equals(this.password)) { this.password = newPassword; changed = true; }
    if (newProfile != this.profile) { this.profile = newProfile; changed = true; }
    return changed;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
    if (status != null && status.getUser() != this) status.linkUser(this);
  }
}