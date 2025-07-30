package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class Base {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateTimestamp() {
        this.updatedAt = Instant.now();
    }

    // 생성 시각 포맷팅
    public String getCreatedAtFormatted() {
        return formatTimestamp(createdAt);
    }

    // 업데이트 시각 포맷팅
    public String getUpdatedAtFormatted() {
        return formatTimestamp(updatedAt);
    }

    // 날짜(시간)포맷팅 로직
    private String formatTimestamp(Instant timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
