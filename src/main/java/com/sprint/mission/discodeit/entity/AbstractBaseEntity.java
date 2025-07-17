package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

/**
 * 엔티티의 공통 ID 기반 슈퍼 클래스.
 */
public abstract class AbstractBaseEntity {
  private final UUID id;

  protected AbstractBaseEntity() {
    this.id = UUID.randomUUID();
  }

  public UUID getId() {
    return id;
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

  @Override
  public String toString() {
    return "AbstractBaseEntity{" + "id=" + id + '}';
  }
}
