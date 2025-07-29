package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public enum ChannelType {
        PUBLIC,
        PRIVATE
    }
    public Channel(){

    }

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String title;
    private String description;
    private ChannelType type;

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }

    public ChannelType getType() {
        return type;
    }

    public void updateType(ChannelType type) {
        this.type = type;
    }
//    private List<UUID> messageId;
//    private List<UUID> userId;




    public String getDescription() {
        return description;
    }

    public void updateDescription(String description) {
        this.description = description;
    }


    public String getTitle() {
        return title;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

//    public void updateMessageId(UUID messageId) {
//        this.messageId.add(messageId);
//    }
//
//    public void updateUserId(UUID userId) {
//        this.userId.add(userId);
//    }
//
//    public List<UUID> getMessageId() {
//        return messageId;
//    }
//
//    public List<UUID> getUserId() {
//        return userId;
//    }




    public UUID getId() {
        return id;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void updateCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }



    public Channel(ChannelType type,String title,String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();
        this.title = title;
        this.description = description;
        this.type=type;
//        messageId = new ArrayList<>();
//        userId = new ArrayList<>();;
    }



}
