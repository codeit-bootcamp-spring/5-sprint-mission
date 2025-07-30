package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final UUID senderId;
    private UUID channelId;

    private String name;
    private String title;
    private String content;

    private long createdAt;
    private long updatedAt;

    public Message(UUID senderId, UUID channelId, String name, String title, String content) {
        this.id = UUID.randomUUID();
        this.senderId = senderId;
        this.channelId = channelId;
        this.name = name;
        this.title = title;
        this.content = content;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = createdAt;
    }

    public long update() {
        return this.updatedAt = Instant.now().getEpochSecond();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
