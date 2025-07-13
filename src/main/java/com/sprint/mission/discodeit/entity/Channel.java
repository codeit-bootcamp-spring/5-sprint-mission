package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private User ownerUser;
    private String name;
    private ChannelType type;
    private String topic;

    private final Long createdAt;
    private Long updatedAt;

    public Channel(
            String name, ChannelType type, User ownerUser,String topic
    ) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
        this.ownerUser = ownerUser;

        this.name = name;
        this.type = type;
        this.topic = topic;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public void updateName(String name) {
        this.name = name;
    }

    public ChannelType getType() {
        return type;
    }

    public void updateType(ChannelType type) {
        this.type = type;
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    public void updateOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser ;
    }

    public String getTopic() {
        return topic;
    }

    public void updateTopic(String topic) {
        this.topic = topic;
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
