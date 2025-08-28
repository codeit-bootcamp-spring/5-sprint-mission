package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
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
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true,
        foreignKey = @ForeignKey(name = "user_statuses_user_id_fkey")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(
        name = "last_active_at",
        nullable = false
    )
    private Instant lastActiveAt;

    public UserStatus(User user) {
        this(user, Instant.now());
    }

    @Override
    public String toString() {
        return "UserStatus[id=%s, userId=%s, lastActiveAt=%s]"
            .formatted(getId(), user != null ? user.getId() : null, lastActiveAt);
    }
}
