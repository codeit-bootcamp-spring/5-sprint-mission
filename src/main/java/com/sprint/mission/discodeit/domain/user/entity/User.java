package com.sprint.mission.discodeit.domain.user.entity;

import com.sprint.mission.discodeit.domain.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.common.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private static final int USERNAME_MAX_LENGTH = 50;
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int PASSWORD_MAX_LENGTH = 60;

    @Column(nullable = false, unique = true, length = USERNAME_MAX_LENGTH)
    private String username;

    @Column(nullable = false, unique = true, length = EMAIL_MAX_LENGTH)
    private String email;

    @Column(nullable = false, length = PASSWORD_MAX_LENGTH)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BinaryContent profile;

    public User(
        String username,
        String email,
        String password,
        BinaryContent profile
    ) {
        if (!hasText(username)) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (!hasText(email)) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (!hasText(password)) {
            throw new IllegalArgumentException("password must not be blank");
        }
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);

        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public User update(
        String newUsername,
        String newEmail,
        String newPassword,
        BinaryContent newProfile
    ) {
        if (newUsername != null) {
            validateUsername(newUsername);
            this.username = newUsername;
        }

        if (newEmail != null) {
            validateEmail(newEmail);
            this.email = newEmail;
        }

        if (newPassword != null) {
            validatePassword(newPassword);
            this.password = newPassword;
        }

        if (newProfile != null) {
            this.profile = newProfile;
        }
        return this;
    }

    public User updateRole(Role newRole) {
        if (newRole != null) {
            this.role = newRole;
        }
        return this;
    }

    private void validateUsername(String username) {
        if (username.length() > USERNAME_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "username must not exceed " + USERNAME_MAX_LENGTH + " characters");
        }
    }

    private void validateEmail(String email) {
        if (email.length() > EMAIL_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "email must not exceed " + EMAIL_MAX_LENGTH + " characters");
        }
    }

    private void validatePassword(String password) {
        if (password.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "encoded password must not exceed " + PASSWORD_MAX_LENGTH + " characters");
        }
    }

    @Override
    public String toString() {
        return "User[id=%s, username=%s, email=%s, profileId=%s]"
            .formatted(getId(), username, email, profile != null ? profile.getId() : null);
    }
}
