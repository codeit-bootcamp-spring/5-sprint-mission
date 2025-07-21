package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity {
  private final UUID id;
  private final long createdAt;
  private long updatedAt;

  protected BaseEntity() {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
  }

  public UUID getId() {
    return id;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void touch() {
    this.updatedAt = System.currentTimeMillis();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseEntity that = (BaseEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
