package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant lastActiveAt;

    public UserStatus(User user, Instant lastActiveAt) {
        this.user = user;
        this.lastActiveAt = lastActiveAt;
    }

    // === 도메인 메서드 ===
    public void update(Instant lastActiveAt) {
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            // updatedArt은 @LastModifiedDate에 의해 자동 세팅됨
        }
    }

    public boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        return lastActiveAt != null && lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
