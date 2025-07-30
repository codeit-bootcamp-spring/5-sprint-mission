package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private UUID userId;
    private UUID channelId;
    private String content;

    public Message(UUID userId,UUID channelId,String content){
        super();
        this.userId=userId;
        this.channelId=channelId;
        this.content=content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }
    public void update(String content){
        super.update();
        this.content=content;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Message{");
        sb.append("userId=").append(userId);
        sb.append(", channelId=").append(channelId);
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
