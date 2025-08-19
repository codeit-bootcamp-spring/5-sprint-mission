package com.sprint.mission.discodeit.dto.response.channel;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class JoinChannelResponse {
	private UUID channelId;
	private String channelName;
	private UUID userId;
	private String userNickname;
	private boolean success;

	private JoinChannelResponse(Channel channel, UUID userId, String userNickname) {
		this.channelId = channel.getId();
		this.channelName = channel.getChannelName();
		this.userId = userId;
		this.userNickname = userNickname;
		this.success = true;
	}

	public static JoinChannelResponse success(Channel channel, UUID userId, String userNickname) {
		return new JoinChannelResponse(channel, userId, userNickname);
	}
}
