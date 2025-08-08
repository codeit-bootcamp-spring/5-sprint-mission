package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID  userId;
    private Instant lastOnlineTime;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
        lastOnlineTime= Instant.now();
    }

    public boolean isOnline() {
        if(lastOnlineTime.isAfter(Instant.now().minusSeconds(300))){
            return true;
        }else{
            return false;
        }
    }

    public void updateLastOnlineTime() {
        this.lastOnlineTime = Instant.now();
        super.updateUpdatedAt();
    }

}
