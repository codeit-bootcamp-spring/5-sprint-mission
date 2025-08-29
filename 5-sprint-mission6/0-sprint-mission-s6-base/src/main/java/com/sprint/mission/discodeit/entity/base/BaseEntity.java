package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public UUID getId() { return id; }
  public Instant getCreatedAt() { return createdAt; }

  // equals/hashCode는 식별자 기반으로
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BaseEntity that)) return false;
    return id != null && id.equals(that.id);
  }
  @Override
  public int hashCode() { return Objects.hashCode(id); }
}
