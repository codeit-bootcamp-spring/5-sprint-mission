package com.sprint.mission.discodeit.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass // 공통 Entity를 알리는 어노테이션
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BaseUpdatableEntity extends BaseEntity {
    @LastModifiedDate
    @Column()
    private Instant updatedAt;

    protected BaseUpdatableEntity(UUID id, Instant createdAt, Instant updatedAt) {
        super(id, createdAt);
        this.updatedAt = updatedAt;
    }
}
