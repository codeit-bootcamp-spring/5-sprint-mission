package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChannelDto(UUID id, ChannelType type, String name, String description,
                         List<UserDto> participants, Instant lastMessageAt) {

}
