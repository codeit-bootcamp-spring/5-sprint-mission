package com.sprint.mission.discodeit.entity;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "message_attachments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_msg_attachments_message_order", columnNames = { "message_id",
            "order_index" })
    }
)
public class MessageAttachment {

    @EmbeddedId
    private MessageAttachmentId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("messageId")
    @JoinColumn(name = "message_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("attachmentId")
    @JoinColumn(name = "attachment_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BinaryContent attachment;

    private int orderIndex;

    public MessageAttachment(Message message, BinaryContent attachment, int orderIndex) {
        this.id = new MessageAttachmentId();
        this.message = message;
        this.attachment = attachment;
        this.orderIndex = orderIndex;
    }

    @Override
    public String toString() {
        return "MessageAttachment[messageId=%s, attachmentId=%s, orderIndex=%s]"
            .formatted(
                message != null ? message.getId() : null,
                attachment != null ? attachment.getId() : null,
                orderIndex
            );
    }
}
