package com.sprint.mission.discodeit.domain.outbox;

import com.sprint.mission.discodeit.infra.outbox.entity.AggregateType;

import java.util.UUID;

public interface OutboxEventWriter {
    void write(
        AggregateType aggregateType,
        UUID aggregateId,
        String topic,
        Object event
    );
}
