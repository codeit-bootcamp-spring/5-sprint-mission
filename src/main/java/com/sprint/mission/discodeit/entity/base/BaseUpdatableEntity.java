package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.MappedSuperclass;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    private Instant updatedAt;
}
