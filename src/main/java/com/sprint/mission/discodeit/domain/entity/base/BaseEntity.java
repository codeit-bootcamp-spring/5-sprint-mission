package com.sprint.mission.discodeit.domain.entity.base;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@SuperBuilder

public abstract class BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	protected UUID id;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	protected Instant createdAt;
}
