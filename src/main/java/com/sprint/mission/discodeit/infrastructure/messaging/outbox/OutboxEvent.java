package com.sprint.mission.discodeit.infrastructure.messaging.outbox;

import com.sprint.mission.discodeit.common.domain.BaseEntity;
import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    public OutboxEvent(
        AggregateType aggregateType,
        UUID aggregateId,
        String topic,
        String payload
    ) {
        if (aggregateType == null) {
            throw new IllegalArgumentException("aggregateType must not be null");
        }
        if (aggregateId == null) {
            throw new IllegalArgumentException("aggregateId must not be null");
        }
        if (topic == null) {
            throw new IllegalArgumentException("topic must not be null");
        }
        if (!hasText(payload)) {
            throw new IllegalArgumentException("payload must not be blank");
        }

        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.topic = topic;
        this.payload = payload;
    }
}
