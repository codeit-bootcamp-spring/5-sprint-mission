package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@Data
@Getter
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  //로그인ID: usermame
  @Column(name = "username", length = 50, nullable = false)
  private String username;

  @Column(name = "email", length = 100, nullable = false)
  private String email;

  @Column(name = "password", length = 60, nullable = false)
  private String password;

  /* 유저와 프로필 이미지 1:1
   * User의 profile_id 칼럼은
   * BinaryContent의 id 칼럼을 참조한다
   */
  @OneToOne
  @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk_users_profile"))
  private BinaryContent profile;

  /* 유저와 유저 상태 1:1
   * 하나의 User는 하나의 UserStatus를 가진다
   * FK관리 (주인: UserStatus)
   */
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status; // User 상태

  /* 유저와 메세지들 1:N
   * 하나의 User는 여러 Message를 가질 수 있다
   * FK관리 (주인: Message)
   */
  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  private List<Message> messages;


  /* 유저의 읽음 상태들 1:N
   * 한 명의 User는 여러 ReadStatus를 가진다
   * FK관리 (주인: ReadStatus)
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<ReadStatus> readStatuses;

  /* 유저와 채널 N:N
   * 여러 유저는 여러 채널을 가질 수 있다
   * FK관리 (주인 : Channel)
   * */
  @ManyToMany(mappedBy = "participants")
  private List<Channel> channels;


  //기본 생성자
  public User() {
    super(); // 생략가능, 기본 생성자 호출됨
  }

  //일반 생성자
  public User(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  //복사본 생성자 - id, createdAt, updatedAt 복사X
  public User(User other) {
    this.username = other.username;
    this.password = other.password;
    this.email = other.email;
    this.profile = other.profile;
    this.status = other.status;
    this.messages = other.messages;
    this.readStatuses = other.readStatuses;
    this.channels = other.channels;
  }

  //toString
  @Override
  public String toString() {
    return "User{" +
        "username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", password='" + password + '\'' +
        ", profile=" + profile +
        ", status=" + status +
        ", messages=" + messages +
        ", readStatuses=" + readStatuses +
        ", channels=" + channels +
        "} " + super.toString();
  }
}