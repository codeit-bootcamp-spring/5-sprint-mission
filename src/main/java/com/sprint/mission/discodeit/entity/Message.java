package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID authorId;
    private UUID channelId;
    private String content;

    public Message(UUID userId,UUID channelId,String content){
        super();
        this.authorId=userId;
        this.channelId=channelId;
        this.content=content;
    }

    public void update(String content){
        super.updateTimestamp();
        this.content=content;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Message{");
        sb.append(super.getId());
        sb.append(" userId=").append(authorId);
        sb.append(", channelId=").append(channelId);
        sb.append(", content='").append(content).append('\'');
        sb.append("'}");
        return sb.toString();
    }
}
