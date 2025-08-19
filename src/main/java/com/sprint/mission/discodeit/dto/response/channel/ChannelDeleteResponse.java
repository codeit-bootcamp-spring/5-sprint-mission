package com.sprint.mission.discodeit.dto.response.channel;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ChannelDeleteResponse {
	private UUID channelId;
	private String name;
	private String type;
	private boolean success;

	private ChannelDeleteResponse(Channel channel) {
		this.channelId = channel.getId();
		this.name = channel.getName();
		this.type = channel.getType();
		this.success = true;
	}

	public static ChannelDeleteResponse success(Channel channel) {
		return new ChannelDeleteResponse(channel);
	}
}
