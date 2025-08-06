package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {


    private  UUID id;
    private  long createdAt;
    private long updatedAt;

    private String channelId;
    private String channelName;


   public Channel(String channelId, String channelName){
        this.id = UUID.randomUUID();
       this.createdAt = System.currentTimeMillis();
       this.updatedAt = this.createdAt;
       this.channelId = channelId;
       this.channelName = channelName;

    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public  void update(String channelId, String channelName, long updatedAt){
        this.channelId = channelId;
        this.channelName= channelName;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                '}';
    }
}
