package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String chName;

    public Channel(String chName){
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this. updatedAt = this.createdAt;
        this. chName = chName;
    }

    public UUID getId(){
        return id;
    }

    public long getCreatedAt(){
        return createdAt;
    }

    public long getUpdatedAt(){
        return updatedAt;
    }

    public String getchName(){
        return chName;
    }

    public void updateChName(String chName){
        this.chName = chName;
        this.updatedAt = System.currentTimeMillis();
    }
}
