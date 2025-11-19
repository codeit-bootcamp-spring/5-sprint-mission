package com.sprint.mission.discodeit.entity;

import java.time.Instant;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "read_statuses")
@AllArgsConstructor
@NoArgsConstructor
public class ReadStatus extends BaseUpdatableEntity {

	private Instant lastReadAt;

	@JoinColumn(name = "user_id", updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@JoinColumn(name = "channel_id", updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Channel channel;

	public void update(Instant newLastReadAt) {
		boolean anyValueUpdated = false;
		if (newLastReadAt != null && this.lastReadAt != newLastReadAt) {
			this.lastReadAt = newLastReadAt;
			anyValueUpdated = true;
		}

		if (anyValueUpdated) {
			super.setUpdatedAt(Instant.now());
		}
	}
}
