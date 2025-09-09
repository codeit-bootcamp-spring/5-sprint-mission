package com.sprint.mission.discodeit.domain.entity.base;

import java.time.Instant;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseUpdatableEntity extends BaseEntity {
	@LastModifiedDate
	protected Instant updatedAt;
}
