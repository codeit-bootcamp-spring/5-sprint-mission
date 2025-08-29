package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter @Setter @SuperBuilder /*@ToString*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;


    @Column()
	private UUID authorId;
    @Column(nullable = false, updatable = false)
	private UUID channelId;
    @Column
	private String content;
    @Column(nullable = false)
	private List<UUID> attachmentIds;

	public Message(UUID authorId, UUID channelId, String content) {
		this.authorId = Objects.requireNonNull(authorId, "작성자UUID는 필수 입니다.");
		this.channelId = Objects.requireNonNull(channelId, "채널UUID는 필수입니다.");
		this.content = Objects.requireNonNull(content, "내용은 필수 입니다.");

		attachmentIds = new ArrayList<>();
	}

	public Message(Message original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
		this.authorId = original.authorId;
		this.channelId = original.channelId;
		this.content = original.content;
		this.attachmentIds = original.attachmentIds;
	}

	public void addAttachment(UUID attachmentId) {
		if (attachmentId != null && !this.attachmentIds.contains(attachmentId)) {
			this.attachmentIds.add(attachmentId);
		}
	}

	public void removeAttachment(UUID attachmentId) {
		this.attachmentIds.remove(attachmentId);
	}

	public Message copy() {
		return new Message(this);
	}
}
