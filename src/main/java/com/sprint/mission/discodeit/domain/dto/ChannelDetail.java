package com.sprint.mission.discodeit.domain.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.Channels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ChannelDetail {
	private final Channels channels;
	private final Instant lastMessageAt;
	private final List<UUID> userIds;

}
