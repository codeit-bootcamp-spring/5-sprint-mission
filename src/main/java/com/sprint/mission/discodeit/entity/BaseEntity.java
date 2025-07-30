package com.sprint.mission.discodeit.entity;

import java.util.UUID;


public class BaseEntity {
  protected final UUID id;
  protected final Long createdAt;
  protected Long updatedAt;

  public BaseEntity(UUID id, Long createdAt, Long updatedAt) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public Long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Long updatedAt) {
    this.updatedAt = updatedAt;
  }

}
