package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_status")
public class UserStatus extends BaseUpdatableEntity{
    @OneToOne(fetch = FetchType.LAZY,orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", unique = true,  nullable = false)
    private User user;
    @Column(name="last_active_at", nullable =false)
    private Instant lastActiveAt;
}
