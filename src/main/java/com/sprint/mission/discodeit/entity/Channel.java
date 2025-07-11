package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {


    private  UUID id;
    private  long createdAt;
    private long updatedAt;

   public Channel(UUID id){
        this.id = id;
       this.createdAt = System.currentTimeMillis();
       this.updatedAt = this.createdAt;
    }

    public  void update(UUID id, long updatedAt){
        this.id = id;

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
        final StringBuffer sb = new StringBuffer("Channel{");
        sb.append("id=").append(id);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }
}
