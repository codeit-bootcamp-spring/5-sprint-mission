package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    private Instant lastActiveAt;

    public UserStatus(User user, Instant lastActiveAt) {
        setUser(user);
        this.lastActiveAt = lastActiveAt;
    }

    public boolean isOnline() {
        Duration duration = Duration.between(lastActiveAt, Instant.now());
        return duration.toMinutes() <= 5;
    }

    public void update(Instant lastAccessAt) {
        if (lastAccessAt != null && !lastAccessAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastAccessAt;
        }
    }

    public void setUser(User user) {
        this.user = user;
        user.setUserStatus(this);
    }
}
