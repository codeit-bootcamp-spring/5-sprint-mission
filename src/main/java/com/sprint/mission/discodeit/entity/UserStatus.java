package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @JoinColumn(name = "user_id")
    private User user;

    private Instant lastActiveAt;

    public UserStatus(User user) {
        this(user, Instant.now());
    }

    public boolean isOnline() {
        return lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }

    @Override
    public String toString() {
        return "UserStatus[id=%s, userId=%s, lastActiveAt=%s]"
            .formatted(getId(), user != null ? user.getId() : null, lastActiveAt);
    }
}
