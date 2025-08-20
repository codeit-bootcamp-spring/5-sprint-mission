package com.sprint.mission.discodeit.domain.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include
  private final UUID id;

  private final Instant createdAt;
  private Instant updatedAt;

  private boolean deleted;
  private Instant deletedAt;
  private Instant purgeAt;

  private long version;

  protected AbstractEntity(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      boolean deleted,
      Instant deletedAt,
      Instant purgeAt,
      long version
  ) {
    final Instant current = Instant.now();

    this.id = (id != null) ? id : UUID.randomUUID();

    this.createdAt = (createdAt != null) ? createdAt : current;
    this.updatedAt = (updatedAt != null) ? updatedAt : this.createdAt;

    this.deleted = deleted;
    if (!deleted) {
      if (deletedAt != null || purgeAt != null) {
        throw new IllegalArgumentException("Not-deleted entity must have null deletedAt/purgeAt");
      }
      this.deletedAt = null;
      this.purgeAt = null;
    } else {
      this.deletedAt = (deletedAt != null) ? deletedAt : current;
      this.purgeAt = purgeAt;
    }
    if (version < 0) {
      throw new IllegalArgumentException("version must be >= 0");
    }
    this.version = version;

    verifyInvariants();
  }

  protected AbstractEntity(UUID id, Instant createdAt) {
    this(id, createdAt, null, false, null, null, 0L);
  }

  protected AbstractEntity(UUID id) {
    this(id, null);
  }

  protected AbstractEntity() {
    this(null);
  }

  public boolean isNotDeleted() {
    return !deleted;
  }

  public void delete() {
    applyDelete(null);
  }

  public void delete(Instant purgeAt) {
    applyDelete(purgeAt);
  }

  public void restore() {
    if (!deleted && deletedAt == null && purgeAt == null) {
      return;
    }
    this.deleted = false;
    this.deletedAt = null;
    this.purgeAt = null;
    touch();
    onRestored();
  }

  public void schedulePurge(Instant purgeAt) {
    Objects.requireNonNull(purgeAt, "purgeAt must not be null");
    if (!deleted) {
      throw new IllegalStateException("Call delete() first");
    }
    if (purgeAt.isBefore(deletedAt)) {
      throw new IllegalArgumentException("purgeAt must be >= deletedAt");
    }
    if (!purgeAt.equals(this.purgeAt)) {
      this.purgeAt = purgeAt;
      touch();
      onPurgeScheduled(purgeAt);
    }
  }

  public void cancelPurge() {
    if (this.purgeAt == null) {
      return;
    }
    this.purgeAt = null;
    touch();
    onPurgeCanceled();
  }

  public boolean shouldPurge() {
    return shouldPurge(Instant.now());
  }

  public boolean shouldPurge(Instant ref) {
    return deleted && purgeAt != null && !purgeAt.isAfter(ref);
  }

  public void requireVersion(long expected) {
    if (this.version != expected) {
      throw new IllegalStateException("버전 불일치: expected=" + expected + ", actual=" + this.version);
    }
  }

  protected void touch() {
    this.updatedAt = Instant.now();
    this.version++;
  }

  protected void touch(Instant updatedAt) {
    this.updatedAt = updatedAt;
    this.version++;
  }

  private void applyDelete(Instant purgeAt) {
    boolean mutated = false;
    Instant current = Instant.now();

    if (!this.deleted) {
      this.deleted = true;
      this.deletedAt = current;
      mutated = true;
      onDeleted();
    }
    if (!Objects.equals(this.purgeAt, purgeAt)) {
      if (purgeAt != null && purgeAt.isBefore(this.deletedAt)) {
        throw new IllegalArgumentException("purgeAt must be >= deletedAt");
      }
      this.purgeAt = purgeAt;
      mutated = true;
      if (purgeAt != null) {
        onPurgeScheduled(purgeAt);
      }
    }
    if (mutated) {
      touch(current);
    }
  }

  private void verifyInvariants() {
    if (updatedAt.isBefore(createdAt)) {
      throw new IllegalStateException("updatedAt must be >= createdAt");
    }
    if (!deleted) {
      if (deletedAt != null || purgeAt != null) {
        throw new IllegalStateException("Non-deleted must have null deletedAt/purgeAt");
      }
    } else {
      if (deletedAt == null) {
        throw new IllegalStateException("Deleted must have non-null deletedAt");
      }
      if (deletedAt.isBefore(createdAt)) {
        throw new IllegalStateException("deletedAt must be >= createdAt");
      }
      if (purgeAt != null && purgeAt.isBefore(deletedAt)) {
        throw new IllegalStateException("purgeAt must be >= deletedAt");
      }
    }
    if (version < 0) {
      throw new IllegalStateException("version must be >= 0");
    }
  }

  protected void onDeleted() {
  }

  protected void onRestored() {
  }

  protected void onPurgeScheduled(Instant purgeAt) {
  }

  protected void onPurgeCanceled() {
  }

  @Serial
  private Object readResolve() {
    verifyInvariants();
    return this;
  }
}
