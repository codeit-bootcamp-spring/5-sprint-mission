package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.List;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

	private String content;

	@JoinColumn(name = "channel_id", updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Channel channel;

	@JoinColumn(name = "author_id", updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User author;

	@ManyToMany
	@JoinTable(
		name = "message_attachments",
		joinColumns = @JoinColumn(name = "message_id"),
		inverseJoinColumns = @JoinColumn(name = "attachment_id")
	)
	private List<BinaryContent> attachments;

	public void update(String content) {
		boolean anyValueUpdated = false;
		if (content != null && !content.equals(this.content)) {
			this.content = content;
			anyValueUpdated = true;
		}

		if (anyValueUpdated) {
			super.setUpdatedAt(Instant.now());
		}
	}
}
