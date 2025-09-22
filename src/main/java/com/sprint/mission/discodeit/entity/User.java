package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Setter
  private UserStatus status;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ReadStatus> readStatuses = new ArrayList<>();


  public void update(UpdateCommand update, BinaryContent profile) {

    if (update.getUsername() != null && !update.getUsername()
                                               .equals(this.username)) {
      this.username = update.getUsername();
    }
    if (update.getEmail() != null && !update.getEmail()
                                            .equals(this.email)) {
      this.email = update.getEmail();
    }
    if (update.getPassword() != null && !update.getPassword()
                                               .equals(this.password)) {
      this.password = update.getPassword();
    }
    if (profile != null && !profile.equals(this.profile)) {
      this.profile = profile;
    }
  }
}
