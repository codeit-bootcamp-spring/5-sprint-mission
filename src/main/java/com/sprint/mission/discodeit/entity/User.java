package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder /*@ToString*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false, length = 50)
    private String username;
    @Column(nullable = false, length = 30)
    @JsonIgnore
    private String password;
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    @Column(nullable = false, length = 100)
    private String defaultNickname;

    // 연관관계

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private UserStatus userStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReadStatus> readStatus;

    @OneToMany(mappedBy = "author")
    private List<Message> messages;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    public User(String username, String password, String defaultNickname, String email, BinaryContent profile) {
        this.username = username;
        this.password = password;
        this.defaultNickname = defaultNickname;
        this.email = email;
        this.profile = profile;
    }

    // 복사용
    public User(User original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
        this.email = original.email;
        this.profile = original.profile;
        this.username = original.username;
        this.password = original.password;
        this.defaultNickname = original.defaultNickname;
        this.userStatus = original.userStatus;
        this.readStatus = original.readStatus;
        this.messages = original.messages;
    }

    public void updateUsername(String username) {
        this.username = Objects.requireNonNull(username, "사용자 이름은 필수 입력값입니다.");
    }

    public void updateEmail(String email) {
        this.email = Objects.requireNonNull(email, "이메일은 필수 입력값입니다.");
    }

    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
    }

    public void updatePassword(String password) {
        this.password = Objects.requireNonNull(password, "비밀번호는 필수 입력값입니다.");
    }

    public void updateDefaultNickname(String defaultNickname) {
        this.defaultNickname = Objects.requireNonNull(defaultNickname, "닉네임은 필수 입력값입니다.");
    }

    public void removeProfile() {
        this.profile = null;
    }

    public User copy() {
        return new User(this);
    }
}
