package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Channel channel;
    private final User authorUser;

    private String content;
    private boolean isPinned;
    private final Long createdAt;
    private Long updatedAt;

    public Message(
            String content, boolean isPinned, Channel channel, User authorUser
    ) {
        this.id = UUID.randomUUID();
        this.channel = channel;
        this.authorUser = authorUser;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;

        this.content = content;
        this.isPinned = isPinned;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getAuthorUser() {
        return authorUser;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void updatePinned(boolean pinned) {
        isPinned = pinned;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

}
