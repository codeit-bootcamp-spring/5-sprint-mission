package com.sprint.mission.discodeit.entity.base;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;


@Getter
public abstract class BaseEntity {
    private UUID id;

    @CreatedDate
    private Instant createdAt;
}
