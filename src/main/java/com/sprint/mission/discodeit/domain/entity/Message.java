package com.sprint.mission.discodeit.domain.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.sprint.mission.discodeit.domain.entity.base.BaseUpdatableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@Entity
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseUpdatableEntity {

	private String content;

	// Foreign key
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User user;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	@ManyToMany
	@JoinTable(
	  name = "message_attachment",
	  joinColumns = @JoinColumn(name = "message_id"),
	  inverseJoinColumns = @JoinColumn(name = "attachment_id")
	)
	private final List<BinaryContent> attachments = new ArrayList<>();

	public Message(String content, @NonNull User user, @NonNull Channel channel, List<BinaryContent> attachments) {
		this.content = content;
		this.user = user;
		this.channel = channel;
		if (attachments != null) {
			this.attachments.addAll(attachments);
		}
	}

	public void setContent(String content) {
		this.content = content;
		this.updatedAt = Instant.now();
	}

}
