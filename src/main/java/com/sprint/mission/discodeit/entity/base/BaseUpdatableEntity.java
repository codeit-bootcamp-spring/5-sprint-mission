package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Getter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {
    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant UpdatedAt;
}
