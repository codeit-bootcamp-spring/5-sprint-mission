package com.sprint.mission.discodeit.entity.base;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@ToString
@MappedSuperclass // 공통 Entity를 알리는 어노테이션
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 보안적인 용도
@SuperBuilder // lombok용 자식이 부모 빌더를 접근할수 있도록 돕는 어노테이션
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate // 마지막 갱신되는 시간으로 갱신되는 어노테이션
    @Column(nullable = true)
    protected Instant updatedAt;
}
