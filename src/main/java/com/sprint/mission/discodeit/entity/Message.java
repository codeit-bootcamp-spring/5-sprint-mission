package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final UUID channelId;

    private final Long createAt;
    private Long modifyAt;

    private final User author;
    private final List<User> mentions;
    private String message;
    private boolean tts;

    public Message(UUID channelId, String message, User author, boolean tts) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createAt = now.getEpochSecond();

        this.mentions = new ArrayList<>();
        this.channelId = channelId;
        this.message = message;
        this.author = author;
        this.tts = tts;
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

    public boolean isTts() { return tts; }

    public void update(Message messageDTO) {
        this.message = messageDTO.message;
        this.tts =  messageDTO.tts;
        this.mentions.clear();
        this.mentions.addAll(messageDTO.mentions);

        Instant now = Instant.now();
        this.modifyAt = now.getEpochSecond();
    }
}
