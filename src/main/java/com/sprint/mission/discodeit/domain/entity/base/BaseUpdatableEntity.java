package com.sprint.mission.discodeit.domain.entity.base;

import java.time.Instant;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public abstract class BaseUpdatableEntity extends BaseEntity {
	@LastModifiedDate
	protected Instant updatedAt;
}
