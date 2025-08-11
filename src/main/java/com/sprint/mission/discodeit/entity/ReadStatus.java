package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
    public void markRead(Instant at){
        if(lastReadAt==null || at.isAfter(lastReadAt)){
            lastReadAt = at;

        }
    }

    public boolean isUnreadSince(Instant messageCreatedAt){
        return lastReadAt == null || messageCreatedAt.isAfter(lastReadAt);
    }


}
