package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity{
    @Column(name="username", nullable=false, unique=true)
    private String username;
    @Column(name="email", nullable=false, unique=true)
    private String email;
    @Column(name="password", length = 60, nullable = false)
    private String password;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profile_id", nullable = true)
    private BinaryContent profile;
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserStatus status;
}
