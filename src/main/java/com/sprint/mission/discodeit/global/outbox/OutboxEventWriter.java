package com.sprint.mission.discodeit.global.outbox;

import java.util.UUID;

public interface OutboxEventWriter {
    void write(
        AggregateType aggregateType,
        UUID aggregateId,
        String topic,
        Object event
    );
}
