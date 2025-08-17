package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class UserStatus extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private UUID userId;
    private Instant lastSeenAt;

    

    public boolean isOnline() {
        return Instant.now().minusSeconds(300).isBefore(lastSeenAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStatus that = (UserStatus) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
