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
import org.hibernate.annotations.BatchSize;

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
    @Column(nullable = false, length = 100)
    @JsonIgnore
    private String password;
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    @Column(nullable = false, length = 100)
    private String defaultNickname;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    // 연관관계

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<ReadStatus> readStatus;

    @OneToMany(mappedBy = "author")
    @BatchSize(size = 100)
    private List<Message> messages;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    public User(String username, String password, String defaultNickname, String email,Role role, BinaryContent profile) {
        this.username = username;
        this.password = password;
        this.defaultNickname = defaultNickname;
        this.email = email;
        this.role = role;
        this.profile = profile;
    }

    // 복사용
    public User(User original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
        this.email = original.email;
        this.role = original.role;
        this.profile = original.profile;
        this.username = original.username;
        this.password = original.password;
        this.defaultNickname = original.defaultNickname;
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

    public void updateRole(Role role) {
        this.role = Objects.requireNonNull(role, "권한은 필수 입력값입니다.");
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
