package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name="read_statuses",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id","channel_id"})
})
public class ReadStatus extends BaseUpdatableEntity{
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="channel_id")
    private Channel channel;
    @Column(name="last_read_at", nullable = false)
    private Instant lastReadAt;
}
