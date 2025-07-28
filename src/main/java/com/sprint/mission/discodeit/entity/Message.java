package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class Message implements Serializable {
    private final UUID id;
    private final UUID channelId;
    private final Long createAt;
    private final UUID author;

    private String message;
    private boolean allMentioned;

    private Long modifyAt;

    public Message(UUID channelId, String message, UUID author, boolean allMentioned) {
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

    public UUID getAuthor() { return author; }

    public boolean isAllMentioned() { return allMentioned; }

    public void update(String message, boolean allMentioned) {
        int sameValueCount = 0;
        if(this.message.equals(message)){
            System.out.println("[Alarm] : The original message and the message to be changed are the same.");
            sameValueCount++;
        }
        if(this.allMentioned == allMentioned){
            System.out.println("[Alarm] : There is no change in all_mentioned value  .");
            sameValueCount++;
        }
        this.message = message;
        this.allMentioned =  allMentioned;

        if (sameValueCount != 2) {
            Instant now = Instant.now();
            this.modifyAt = now.getEpochSecond();
        }
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
