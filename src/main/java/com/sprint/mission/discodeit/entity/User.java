package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseUpdatableEntity {
    private String username; // 사용자명 (아이디)
    private String email;
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BinaryContent profile;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus userStatus;

    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
        }

        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }

        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }

        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
        }
    }
}
