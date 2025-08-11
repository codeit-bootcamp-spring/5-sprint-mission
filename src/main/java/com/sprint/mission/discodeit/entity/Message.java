package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID authorId;
    private UUID channelId;
    private String content;
    private List<UUID> attachmentIds;

    public Message(UUID userId,UUID channelId,String content,List<UUID> attachmentIds) {
        super();
        this.authorId=userId;
        this.channelId=channelId;
        this.content=content;
        this.attachmentIds=attachmentIds;
    }

    public void update(String newContent){
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.updateTimestamp();
        }
    }
}
