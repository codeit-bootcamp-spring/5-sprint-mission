package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * 수정 시각을 추가로 가지는 추상 클래스.
 * - @LastModifiedDate: 업데이트 시각 자동 반영
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    @Column(nullable = false)
    @Getter
    private Instant updatedAt;
}
