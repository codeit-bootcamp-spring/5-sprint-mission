package com.sprint.mission.discodeit.entity;

import java.time.Instant;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;

import jakarta.persistence.Column;
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

	@Column(nullable = false)
	private boolean notificationEnabled;

	public ReadStatus(Instant lastReadAt, User user, Channel channel) {
		this.lastReadAt = lastReadAt;
		this.user = user;
		this.channel = channel;
		this.notificationEnabled = false;
	}

	public void update(Instant newLastReadAt, Boolean newNotificationEnabled) {
		if (newLastReadAt != null) {
			this.lastReadAt = newLastReadAt;
		}
		if (newNotificationEnabled != null) {
			this.notificationEnabled = newNotificationEnabled;
		}
	}
}
