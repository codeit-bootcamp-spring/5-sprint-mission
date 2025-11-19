package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
    name = "read_statuses",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_read_statuses", columnNames = { "user_id", "channel_id" })
    }
)
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Channel channel;

    @Column(nullable = false)
    private Instant lastReadAt;

    @Override
    public String toString() {
        return "ReadStatus[userId=%s, channelId=%s, lastReadAt=%s]"
            .formatted(
                user != null ? user.getId() : null,
                channel != null ? channel.getId() : null,
                lastReadAt
            );
    }
}
