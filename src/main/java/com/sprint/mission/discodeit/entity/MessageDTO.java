package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class MessageDTO {
    private UUID id;
    private UUID channelId;
    private User author;
    private String message;
    private boolean allMentioned;

    public MessageDTO(UUID id, UUID channelId, String message, User author, boolean allMentioned) {
        this.id = id;
        this.channelId = channelId;
        this.author = author;
        this.message = message;
        this.allMentioned = allMentioned;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAllMentioned() {
        return allMentioned;
    }

    public void setAllMentioned(boolean allMentioned) {
        this.allMentioned = allMentioned;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageDTO{");
        sb.append("id=").append(id);
        sb.append(", message='").append(message).append('\'');
        sb.append(", channelId=").append(channelId);
        sb.append(", \nauthor=").append(author);
        sb.append(", \nallMentioned=").append(allMentioned);
        sb.append('}');
        return sb.toString();
    }
}
