package com.sprint.mission.discodeit.common.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@MappedSuperclass
@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    private Instant updatedAt;
}
