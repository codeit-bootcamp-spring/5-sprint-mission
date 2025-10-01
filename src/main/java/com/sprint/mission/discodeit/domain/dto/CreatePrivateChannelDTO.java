package com.sprint.mission.discodeit.domain.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CreatePrivateChannelDTO {

	private final List<UUID> userIds;

}
