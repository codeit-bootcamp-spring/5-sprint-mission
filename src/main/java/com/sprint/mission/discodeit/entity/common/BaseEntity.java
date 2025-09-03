package com.sprint.mission.discodeit.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

@Getter
@MappedSuperclass
public class BaseEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  Instant createdAt;

}

