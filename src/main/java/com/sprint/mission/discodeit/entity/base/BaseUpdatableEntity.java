// src/main/java/com/sprint/mission/discodeit/entity/base/BaseUpdatableEntity.java
package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  protected Instant updatedAt;

  protected BaseUpdatableEntity() { }

  protected BaseUpdatableEntity(java.util.UUID id) {
    super(id);
  }

  @PreUpdate
  protected void onUpdateFallback() {
    this.updatedAt = Instant.now();
  }
}
