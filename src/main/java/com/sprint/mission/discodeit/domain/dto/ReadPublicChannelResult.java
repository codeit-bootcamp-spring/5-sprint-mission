package com.sprint.mission.discodeit.domain.dto;

import com.sprint.mission.discodeit.domain.entity.Channels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ReadPublicChannelResult {
	Channels channels;
}
