package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity // JPA 엔티티 객체로 지정
@Table(name = "users")
@Getter @Setter @SuperBuilder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity{

    private String username;
    private String email;
    private String password;

    @OneToOne(fetch = FetchType.LAZY, // 1:1 : User당 하나의 프로필만! DB조회
        cascade = CascadeType.ALL, // User를 저장/삭제 시 -> Profile도 같이 저장/삭제됨
        orphanRemoval = true) // User에서 Profile=null 이면 -> DB에서 자동 삭제
    @JoinColumn(name = "profile_id") // FK
    private BinaryContent profileImage;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // 양방향 매핑..
    private UserStatus userStatus;


    public User(String username, String email, String password, BinaryContent profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

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
        if (newProfile != null) {
            this.profileImage = newProfile;
        }
    }
}
