package com.sprint.mission.discodeit.domain.entity;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.base.BaseUpdatableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Entity
public class ReadStatus extends BaseUpdatableEntity {

	@JoinColumn(nullable = false)
	private Instant lastReadAt;

	// Foreign key
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	public ReadStatus(@NonNull UUID userId, @NonNull UUID channelId, Instant lastReadAt) {
		this.id = UUID.randomUUID();
		this.createdAt = Instant.now();
		// this.userId = userId;
		// this.channelId = channelId;
		this.lastReadAt = lastReadAt;
	}

	public void setLastReadAt(Instant newLsatReadAt) {
		this.lastReadAt = newLsatReadAt;
		this.updatedAt = Instant.now();

	}
}
