package com.sprint.mission.discodeit.readstatus.domain;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.common.domain.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
    name = "read_statuses",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_read_statuses", columnNames = {"user_id", "channel_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Channel channel;

    @Column(nullable = false)
    private Instant lastReadAt;

    private boolean notificationEnabled;

    public ReadStatus(
        User user,
        Channel channel,
        Instant lastReadAt,
        boolean notificationEnabled
    ) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        if (channel == null) {
            throw new IllegalArgumentException("channel must not be null");
        }
        if (lastReadAt == null) {
            throw new IllegalArgumentException("lastReadAt must not be null");
        }

        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
        this.notificationEnabled = notificationEnabled;
    }

    public ReadStatus update(Instant newLastReadAt, Boolean newNotificationEnabled) {
        if (newLastReadAt != null) {
            this.lastReadAt = newLastReadAt;
        }

        if (newNotificationEnabled != null) {
            this.notificationEnabled = newNotificationEnabled;
        }

        return this;
    }
}
