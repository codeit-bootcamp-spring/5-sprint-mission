package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String content;
    //
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds; // BinaryContent를 위존하기 위해 사용됨 (User, Message)

    public Message (String content, UUID channelId, UUID authorId, List<UUID> attachmentIds){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
    }

    public void update (String newContent){
        boolean anyValueUpdated = false;
        if (newContent != null && newContent.equals(this.content)) {
            this.content = newContent;
        }
        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
