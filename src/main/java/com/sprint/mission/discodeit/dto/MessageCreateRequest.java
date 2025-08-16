package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageCreateRequest {
    private String content;
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;
}
