package com.sprint.mission.discodeit.entity.base;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


public abstract class BaseUpdatableEntity extends BaseEntity {
    @Getter
    @Setter
    private Instant updatedAt;

    public BaseUpdatableEntity() {
    }
}
