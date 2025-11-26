package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToOne(
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BinaryContent profile;

    @OneToOne(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private UserStatus userStatus;

    public User(
        String username,
        String email,
        String password,
        BinaryContent profile
    ) {
        if (!hasText(username)) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("username must not exceed 50 characters");
        }
        if (!hasText(email)) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("email must not exceed 100 characters");
        }
        if (!hasText(password)) {
            throw new IllegalArgumentException("password must not be blank");
        }
        if (password.length() > 60) {
            throw new IllegalArgumentException("encoded password must not exceed 60 characters");
        }

        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.userStatus = new UserStatus(this);
    }

    public void update(
        String newUsername,
        String newEmail,
        String newPassword,
        BinaryContent newProfile
    ) {
        if (newUsername != null) {
            if (newUsername.length() > 50) {
                throw new IllegalArgumentException("newUsername must not exceed 50 characters");
            }
            this.username = newUsername;
        }

        if (newEmail != null) {
            if (newEmail.length() > 100) {
                throw new IllegalArgumentException("newEmail must not exceed 100 characters");
            }
            this.email = newEmail;
        }

        if (newPassword != null) {
            if (newPassword.length() > 60) {
                throw new IllegalArgumentException("encoded newPassword must not exceed 60 characters");
            }
            this.password = newPassword;
        }

        if (newProfile != null) {
            this.profile = newProfile;
        }
    }

    @Override
    public String toString() {
        return "User[id=%s, username=%s, email=%s, profileId=%s]"
            .formatted(getId(), username, email, profile != null ? profile.getId() : null);
    }
}
