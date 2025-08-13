package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity implements Serializable {
    //사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt; // 마지막으로 읽은 시간

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
    public void update(Instant newLastReadAt){
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.updateTimestamp();
        }
    }
}
