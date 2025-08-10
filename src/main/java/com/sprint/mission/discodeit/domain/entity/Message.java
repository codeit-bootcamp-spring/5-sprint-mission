package com.sprint.mission.discodeit.domain.entity;


import com.sprint.mission.discodeit.util.Validators;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    private final UUID authorId;
    private String content;
    private final Set<UUID> attachmentIds = new LinkedHashSet<>();
    private final UUID replyTo;
    private final UUID chatRoomId;


    public Message(UUID chatRoomId, UUID authorId, String content, Set<UUID> attachmentIds, UUID replyTo) {
        this.chatRoomId = Objects.requireNonNull(chatRoomId, "Chat room id must not be null.");
        this.authorId = Objects.requireNonNull(authorId, "Sender id must not be null.");
        setContent(content);
        setAttachmentIds(attachmentIds);
        if (replyTo != null && replyTo.equals(getId()))
            throw new IllegalArgumentException("Message cannot reply to itself.");
        this.replyTo = replyTo;
    }

    public Message(UUID chatRoomId, UUID authorId, String content, Set<UUID> attachmentIds) {
        this(chatRoomId, authorId, content, attachmentIds, null);
    }

    public Message(UUID chatRoomId, UUID authorId, String content) {
        this(chatRoomId, authorId, content, new LinkedHashSet<>(), null);
    }

    public void setContent(String content) {
        this.content = Validators.validateMessageContent(content);
        touch();
    }

    public Set<UUID> getAttachmentIds() {
        return Collections.unmodifiableSet(attachmentIds);
    }

    public void setAttachmentIds(Set<UUID> attachmentIds) {
        boolean changed = false;
        this.attachmentIds.clear();
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            this.attachmentIds.addAll(attachmentIds);
            changed = true;
        }
        if (changed) touch();
    }

    @Override
    public String toString() {
        return String.format("Message[chatRoom=%s, sender=%s, content='%s', files=%d]",
                chatRoomId, authorId, content, attachmentIds.size());
    }
}
