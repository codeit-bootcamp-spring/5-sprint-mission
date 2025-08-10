package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChannelCreateRequest {
    private List<UUID> participantIds;
}
