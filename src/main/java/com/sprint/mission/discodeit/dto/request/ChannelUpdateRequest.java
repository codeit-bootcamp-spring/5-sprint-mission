package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Getter
@NoArgsConstructor
public class ChannelUpdateRequest {
    private UUID id;
    private String name;
    private String description;
}
