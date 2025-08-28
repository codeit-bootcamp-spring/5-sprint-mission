package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

    // @OneToOne(
    //   mappedBy = "user",
    //   cascade = CascadeType.ALL,
    //   orphanRemoval = true
    // )
    // private UserStatus status;

    @Column(
        length = 50,
        nullable = false,
        unique = true
    )
    private String username;

    @Column(
        length = 100,
        nullable = false,
        unique = true
    )
    private String email;

    @Column(
        length = 60,
        nullable = false
    )
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "profile_id",
        foreignKey = @ForeignKey(name = "users_profile_id_fkey")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private BinaryContent profile;

    @Override
    public String toString() {
        return "User[id=%s, username=%s, email=%s, profileId=%s]"
            .formatted(getId(), username, email, profile != null ? profile.getId() : null);
    }
}
