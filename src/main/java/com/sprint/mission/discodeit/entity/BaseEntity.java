package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity {
  private final UUID id;
  private final long createdAt;
  private long updatedAt;
  private boolean deleted;

  protected BaseEntity(UUID id, long createdAt, long updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.createdAt = createdAt > 0 ? createdAt : System.currentTimeMillis();
    this.updatedAt = updatedAt > 0 ? updatedAt : this.createdAt;
  }

  protected BaseEntity(UUID id, long createdAt) {
    this(id, createdAt, createdAt);
  }

  protected BaseEntity() {
    this(UUID.randomUUID(), System.currentTimeMillis());
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

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
    touch();
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

  @Override
  public String toString() {
    return "BaseEntity{"
        + "id="
        + id
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + ", deleted="
        + deleted
        + '}';
  }
}
