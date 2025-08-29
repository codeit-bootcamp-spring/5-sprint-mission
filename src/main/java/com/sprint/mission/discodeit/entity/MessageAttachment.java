package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@IdClass(MessageAttachmentId.class)
@Table(name = "message_attachments")
public class MessageAttachment {

    @Id
    @EqualsAndHashCode.Include
    private UUID messageId;

    @Id
    @EqualsAndHashCode.Include
    private UUID attachmentId;

    private int orderIndex;

    @Override
    public String toString() {
        return "MessageAttachment[messageId=%s, attachmentId=%s, orderIndex=%s]"
            .formatted(messageId, attachmentId, orderIndex);
    }
}
