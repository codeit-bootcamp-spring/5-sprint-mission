package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;

    @Column
	private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", updatable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    private Channel channel;

    @OneToMany(mappedBy = "message", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MessageAttachment> attachments = new ArrayList<>();

    public Message(User author, Channel channel,String content) {
        this.author = author;
        this.channel = channel;
        this.content = content;
    }

	public Message(Message original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
		this.author = original.author;
		this.channel = original.channel;
		this.content = original.content;
		this.attachments = original.attachments;
	}

    public void addAttachment(BinaryContent attachment) {
        if (attachment == null) return;
        if (attachments == null) attachments = new ArrayList<>();
        boolean exists = attachments.stream()
                .anyMatch(ma -> ma.getAttachment().getId().equals(attachment.getId()));
        if (!exists) {
            attachments.add(new MessageAttachment(this, attachment));
        }
    }

	public void removeAttachment(UUID attachmentId) {
        if (attachments == null || attachmentId == null) return;
        attachments.removeIf(ma -> ma.getAttachment().getId().equals(attachmentId));
	}

	public Message copy() {
		return new Message(this);
	}
}
