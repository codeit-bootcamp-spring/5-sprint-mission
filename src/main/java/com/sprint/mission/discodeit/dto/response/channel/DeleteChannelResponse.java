package com.sprint.mission.discodeit.dto.response.channel;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class DeleteChannelResponse {
	private UUID channelId;
	private String channelName;
	private String type;
	private boolean success;

	private DeleteChannelResponse(Channel channel) {
		this.channelId = channel.getId();
		this.channelName = channel.getChannelName();
		this.type = channel.getType();
		this.success = true;
	}

	public static DeleteChannelResponse success(Channel channel) {
		return new DeleteChannelResponse(channel);
	}
}
