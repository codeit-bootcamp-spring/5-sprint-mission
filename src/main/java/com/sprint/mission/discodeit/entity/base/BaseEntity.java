package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass // 자식 엔티티들이 공통 필드 상속받을 때 사용
@EntityListeners(AuditingEntityListener.class) // 자동 날짜 필드 생성
@SuperBuilder
public abstract class BaseEntity {

  @Id // PK
  @GeneratedValue(strategy = GenerationType.UUID) // DB가 key 자동생성
  @Column(nullable = false, updatable = false) // not null, update 쿼리 수정 불가
  private UUID id;

  @CreatedDate // 자동으로 insert 시 시간 자동 저장
  @Column(columnDefinition ="timestamp with time zone default now()",
              nullable = false, updatable = false)
  private Instant createdAt;

}
