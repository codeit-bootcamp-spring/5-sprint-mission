package com.sprint.mission.discodeit.domain.entityprod;

import com.sprint.mission.discodeit.util.Validators;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "messages")
@Check(constraints = "LENGTH(content) <= 4000")
public class ProdMessage extends ProdBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private ProdUser sender;

    @Column(length = 4000)
    private String content;

    @ElementCollection
    @CollectionTable(name = "message_files", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "file_url")
    private final Set<UUID> attachmentIds = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to")
    private ProdMessage replyTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ProdChatRoom chatRoom;

    public ProdMessage(ProdChatRoom chatRoom, ProdUser sender, String content, Set<UUID> attachmentIds, ProdMessage replyTo) {
        this.chatRoom = Objects.requireNonNull(chatRoom, "Chat room must not be null.");
        this.sender = Objects.requireNonNull(sender, "Sender must not be null.");
        setContent(content);
        setAttachmentIds(attachmentIds);
        this.replyTo = replyTo;
    }

    public void setContent(String content) {
        this.content = Validators.validateMessageContent(content);
    }


    public void setAttachmentIds(Set<UUID> attachmentIds) {
        this.attachmentIds.clear();
        if (attachmentIds == null || attachmentIds.isEmpty()) return;
        this.attachmentIds.addAll(attachmentIds);
    }

    @Override
    public String toString() {
        return String.format("Message[chatRoom=%s, sender=%s, content='%s', files=%d, ]",
                chatRoom, sender, content, attachmentIds.size());
    }
}
