package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageResponse {
    private UUID id;
    private String content;
    private UUID channelId;
    private UUID authorId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<UUID> attachmentIds;
}
