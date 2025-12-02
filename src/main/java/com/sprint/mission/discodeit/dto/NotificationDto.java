package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationDto {
    private UUID id;
    private Instant createdAt;
    private UUID receiverId;
    private String title;
    private String content;
}