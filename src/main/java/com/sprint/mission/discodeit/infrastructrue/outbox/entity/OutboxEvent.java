package com.sprint.mission.discodeit.infrastructrue.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Table(name = "outbox_events")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @CreatedDate
    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false, columnDefinition = "jsonb")
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
