package com.sprint.mission.discodeit.message.domain.attachment;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageAttachmentId implements Serializable {

    private UUID messageId;
    private UUID attachmentId;

    public MessageAttachmentId(
        UUID messageId,
        UUID attachmentId
    ) {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId must not be null");
        }
        if (attachmentId == null) {
            throw new IllegalArgumentException("attachmentId must not be null");
        }

        this.messageId = messageId;
        this.attachmentId = attachmentId;
    }
}
