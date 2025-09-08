package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * 모든 엔티티가 공통으로 가지는 ID, 생성시각.
 * - @MappedSuperclass: 하위 엔티티에 컬럼으로 포함
 * - @EntityListeners + @CreatedDate: JPA Auditing으로 createdAt 자동 세팅
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // UUID 자동 생성
    @Getter
    private UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Getter
    private Instant createdAt;

    protected BaseEntity() {} // 이걸 왜 만들지? 그리고 왜 protected이지?

}
