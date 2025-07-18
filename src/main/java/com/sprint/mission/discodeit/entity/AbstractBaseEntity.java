package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractBaseEntity {
  private final UUID id;
  private final long createdAt;
  private long updatedAt;

  protected AbstractBaseEntity() {
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

  public void setUpdatedAt(long updatedAt) {
    if (this.updatedAt < updatedAt) this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AbstractBaseEntity that)) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
