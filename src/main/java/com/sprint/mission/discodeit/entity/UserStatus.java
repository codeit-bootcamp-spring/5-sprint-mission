package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user) {
        this(user, Instant.now());
    }

    public boolean isOnline() {
        return this.isOnline(Instant.now().minus(Duration.ofMinutes(5)));
    }

    public boolean isOnline(Instant onlineSince) {
        return lastActiveAt.isAfter(onlineSince);
    }

    @Override
    public String toString() {
        return "UserStatus[id=%s, userId=%s, lastActiveAt=%s]"
            .formatted(getId(), user != null ? user.getId() : null, lastActiveAt);
    }
}
