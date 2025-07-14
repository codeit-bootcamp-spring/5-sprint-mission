package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Base {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }


    public UUID getId() {return id;}
    public Long getCreatedAt() {return createdAt;}
    public Long getUpdatedAt() {return updatedAt;}
    public void updateTimestamp() {this.updatedAt = System.currentTimeMillis();}

    public String getCreatedAtFormatted() {
        return formatTimestamp(createdAt);
    }

    public String getUpdatedAtFormatted() {
        return formatTimestamp(updatedAt);
    }

    private String formatTimestamp(Long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getFormattedCreatedAt() {
        return formatTimestamp(createdAt);
    }

    public String getFormattedUpdatedAt() {
        return formatTimestamp(updatedAt);
    }

}
