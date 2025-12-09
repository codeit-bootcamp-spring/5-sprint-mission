package com.sprint.mission.discodeit.user.domain;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.common.domain.BaseUpdatableEntity;
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

    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int ENCODED_PASSWORD_MAX_LENGTH = 60;
    public static final int RAW_PASSWORD_MAX_LENGTH = 50;

    @Column(nullable = false, unique = true, length = USERNAME_MAX_LENGTH)
    private String username;

    @Column(nullable = false, unique = true, length = EMAIL_MAX_LENGTH)
    private String email;

    @Column(nullable = false, length = ENCODED_PASSWORD_MAX_LENGTH)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BinaryContent profile;

    public User(String username, String email, String password, BinaryContent profile) {
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

    public User updateInfo(String newUsername, String newEmail) {
        if (hasText(newUsername)) {
            validateUsername(newUsername);
            this.username = newUsername;
        }
        if (hasText(newEmail)) {
            validateEmail(newEmail);
            this.email = newEmail;
        }
        return this;
    }

    public User updatePassword(String newEncodedPassword) {
        if (hasText(newEncodedPassword)) {
            validatePassword(newEncodedPassword);
            this.password = newEncodedPassword;
        }
        return this;
    }

    public User updateProfile(BinaryContent newProfile) {
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
                "username must not exceed " + USERNAME_MAX_LENGTH);
        }
    }

    private void validateEmail(String email) {
        if (email.length() > EMAIL_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "email must not exceed " + EMAIL_MAX_LENGTH);
        }
    }

    private void validatePassword(String password) {
        if (password.length() > ENCODED_PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "encoded password must not exceed " + ENCODED_PASSWORD_MAX_LENGTH);
        }
    }
}
