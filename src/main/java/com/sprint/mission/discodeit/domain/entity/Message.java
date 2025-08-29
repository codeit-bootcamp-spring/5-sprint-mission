package com.sprint.mission.discodeit.domain.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.base.BaseUpdatableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@Entity
public class Message extends BaseUpdatableEntity {

	private String content;

	// Foreign key
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User user;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	public Message(String content, @NonNull UUID authorId, @NonNull UUID channelId) {
		this.id = UUID.randomUUID();
		this.createdAt = Instant.now();
		this.updatedAt = null;
		this.content = content;
		// this.authorId = authorId;
		// this.channelId = channelId;
	}

	public Instant getLastEditedAt() {
		return updatedAt != null ? updatedAt : createdAt;
	}

	public void setContent(String content) {
		this.content = content;
		this.updatedAt = Instant.now();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Message message = (Message)o;
		return Objects.equals(id, message.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
