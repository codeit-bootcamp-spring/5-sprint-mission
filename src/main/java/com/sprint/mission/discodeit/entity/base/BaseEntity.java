package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  protected UUID id;
  @CreatedDate
  protected Instant createdAt;

  protected BaseEntity() { }

  protected BaseEntity(UUID id) {
    this.id = id;
  }

  @PrePersist
  protected void onCreateFallback() {
    if (this.id == null) this.id = UUID.randomUUID();
    if (this.createdAt == null) this.createdAt = Instant.now();
  }
}
