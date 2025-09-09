package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Column(nullable = false, updatable = false)
  protected UUID id;
  @CreatedDate
  @Column(nullable = false, updatable = false)
  protected Instant createdAt;

  public BaseEntity() {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
  }
}