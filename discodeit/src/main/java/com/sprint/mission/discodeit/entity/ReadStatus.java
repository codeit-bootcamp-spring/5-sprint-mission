package com.sprint.mission.discodeit.entity;

//사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

//@Data
@Getter
@Setter
@ToString
public class ReadStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private UUID channelId;
    private Instant readTime;

    public ReadStatus() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
    public ReadStatus(Instant readTime) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.readTime = readTime;
    }

    public ReadStatus(User user) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = user.getId();
    }
    public ReadStatus(Channel channel) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.channelId = channel.getId();
    }




}
