package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity{

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
