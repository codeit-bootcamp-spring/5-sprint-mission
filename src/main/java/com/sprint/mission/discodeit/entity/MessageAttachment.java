package com.sprint.mission.discodeit.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "message_attachments")
public class MessageAttachment {

    @EmbeddedId
    private MessageAttachmentId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("attachmentId")
    @JoinColumn(name = "attachment_id")
    private BinaryContent attachment;

    private int orderIndex;

    public MessageAttachment(Message message, BinaryContent attachment, int orderIndex) {
        this.id = new MessageAttachmentId(message.getId(), attachment.getId());
        this.message = message;
        this.attachment = attachment;
        this.orderIndex = orderIndex;
    }

    @Override
    public String toString() {
        return "MessageAttachment[messageId=%s, attachmentId=%s, orderIndex=%s]"
            .formatted(message.getId(), attachment.getId(), orderIndex);
    }
}
