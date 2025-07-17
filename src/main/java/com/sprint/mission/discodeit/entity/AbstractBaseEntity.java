package com.sprint.mission.discodeit.entity;

import java.util.UUID;

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
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractBaseEntity that = (AbstractBaseEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
