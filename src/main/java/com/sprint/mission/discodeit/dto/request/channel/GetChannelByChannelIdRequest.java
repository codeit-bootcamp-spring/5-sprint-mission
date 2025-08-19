package com.sprint.mission.discodeit.dto.request.channel;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetChannelByChannelIdRequest {
	private UUID id;
}
