package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.pk.MessageAttachmentId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "message_attachments")
@IdClass(MessageAttachmentId.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MessageAttachment {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Message message;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BinaryContent attachment;
}