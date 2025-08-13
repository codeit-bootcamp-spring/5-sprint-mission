package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    private final User user;
    private final Channel channel;
    private List<UUID> attachmentIds;
    private String content;

    public Message(User user, Channel channel, String content, List<UUID> attachmentIds) {
        this(UUID.randomUUID(), user, channel, content, attachmentIds, Instant.now());
    }

    public Message(UUID id, User user, Channel channel, String content, List<UUID> attachmentIds, Instant createAt) {
        super(id, createAt);
        this.user = user;
        this.channel = channel;
        this.content = content;
        this.attachmentIds = attachmentIds;
    }

    public void updateMessage(String message) {
        this.content = message;
        super.updateTimeStamp();
    }
}
