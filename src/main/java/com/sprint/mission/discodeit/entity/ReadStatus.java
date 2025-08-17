package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    // Simple constructor for testing serialization
    public ReadStatus() {
        super();
    }

    public ReadStatus(UUID id, Instant createdAt, Instant updatedAt, UUID userId, UUID channelId, Instant lastReadAt) {
        super(id, createdAt, updatedAt);
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadStatus that = (ReadStatus) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
