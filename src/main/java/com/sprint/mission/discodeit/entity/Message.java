package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final UUID channelId;

    private final Long createAt;
    private Long modifyAt;

    private final User author;
    private String message;
    private boolean allMentioned;

    public Message(UUID channelId, String message, User author, boolean allMentioned) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createAt = now.getEpochSecond();

        this.channelId = channelId;
        this.message = message;
        this.author = author;
        this.allMentioned = allMentioned;
    }

    public UUID getId() {
        return id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public long getModifyAt() {
        return modifyAt;
    }

    public UUID getChannelId() { return channelId; }

    public String getMessage() { return message; }

    public User getAuthor() { return author; }

    public boolean isAllMentioned() { return allMentioned; }

    public void update(MessageDTO messageDTO) {
        this.message = messageDTO.getMessage();
        this.allMentioned =  messageDTO.isAllMentioned();

        Instant now = Instant.now();
        this.modifyAt = now.getEpochSecond();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("id=").append(id);
        sb.append(",\n message='").append(message).append('\'');
        sb.append(",\n channelId=").append(channelId);
        sb.append(",\n author=").append(author);
        sb.append(",\n createAt=").append(createAt);
        sb.append(", modifyAt=").append(modifyAt);
        sb.append(", allMentioned=").append(allMentioned);
        sb.append('}');
        return sb.toString();
    }
}
