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

    private final UUID channelId;
    private final UUID authorId;
    private final UUID replyTo;

    private String content;
    private final Set<UUID> attachmentIds = new LinkedHashSet<>();

    public Message(UUID channelId, UUID authorId, String content, Set<UUID> attachmentIds, UUID replyTo) {
        this.channelId = Objects.requireNonNull(channelId, "channelId must not be null.");
        this.authorId = Objects.requireNonNull(authorId, "authorId must not be null.");

        setContent(content);
        setAttachmentIds(attachmentIds);

        if (replyTo != null && replyTo.equals(getId())) {
            throw new IllegalArgumentException("Message cannot reply to itself.");
        }
        this.replyTo = replyTo;
    }

    public Message(UUID channelId, UUID authorId, String content, Set<UUID> attachmentIds) {
        this(channelId, authorId, content, attachmentIds, null);
    }

    public Message(UUID channelId, UUID authorId, String content) {
        this(channelId, authorId, content, new LinkedHashSet<>(), null);
    }

    public void setContent(String content) {
        String normalized = Validators.validateMessageContent(content);
        if (!Objects.equals(this.content, normalized)) {
            this.content = normalized;
            touch();
        }
    }

    public void setAttachmentIds(Set<UUID> attachmentIds) {
        Set<UUID> target = (attachmentIds == null) ? Set.of() : attachmentIds;
        if (target.contains(null)) throw new NullPointerException("attachmentId must not be null");

        if (!this.attachmentIds.equals(target)) {
            this.attachmentIds.clear();
            this.attachmentIds.addAll(target);
            touch();
        }
    }

    public boolean addAttachment(UUID id) {
        Objects.requireNonNull(id, "attachmentId must not be null");
        boolean added = this.attachmentIds.add(id);
        if (added) touch();
        return added;
    }

    public boolean removeAttachment(UUID id) {
        Objects.requireNonNull(id, "attachmentId must not be null");
        boolean removed = this.attachmentIds.remove(id);
        if (removed) touch();
        return removed;
    }

    public Set<UUID> getAttachmentIds() {
        return Collections.unmodifiableSet(attachmentIds);
    }

    @Override
    public String toString() {
        return "Message[channel=%s, author=%s, content='%s', attachments=%d]"
                .formatted(channelId, authorId, content, attachmentIds.size());
    }
}
