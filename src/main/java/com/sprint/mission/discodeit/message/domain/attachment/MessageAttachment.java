package com.sprint.mission.discodeit.message.domain.attachment;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.message.domain.Message;
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

@Entity
@Table(
    name = "message_attachments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_msg_attachments_message_order",
            columnNames = {"message_id", "order_index"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public MessageAttachment(
        Message message,
        BinaryContent attachment,
        int orderIndex
    ) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        if (attachment == null) {
            throw new IllegalArgumentException("attachment must not be null");
        }

        this.id = new MessageAttachmentId();
        this.message = message;
        this.attachment = attachment;
        this.orderIndex = orderIndex;
    }
}
