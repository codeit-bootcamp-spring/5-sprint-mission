package com.sprint.mission.discodeit.entity.base;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseUpdatableEntity extends BaseEntity{

  @LastModifiedDate
  @Column
  private Instant updatedAt;

  protected BaseUpdatableEntity(UUID id, Instant createdAt, Instant updatedAt) {
    super(id, createdAt);
    this.updatedAt = updatedAt;
  }

  public void updateTimestamp(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
