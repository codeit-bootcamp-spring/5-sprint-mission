package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(
    name = "read_statuses",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_read_status",
        columnNames = {"user_id", "channel_id"}
    )
)
public class ReadStatus extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "read_statuses_user_id_fkey"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "channel_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "read_statuses_channel_id_fkey")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;

    @Column(
        name = "last_read_at",
        nullable = false
    )
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
