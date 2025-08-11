package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class UseStatus extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    Instant lastSeenAt;

    public void seen(Instant at){
        this.lastSeenAt = at;

    }
}

