package com.sprint.mission.discodeit.dto.response.channel;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChannelLeaveResponse {
	private UUID channelId;
	private String name;
	private UUID userId;
	private String userNickname;
	private boolean success;

	private ChannelLeaveResponse(Channel channel, UUID userId, String userNickname) {
		this.channelId = channel.getId();
		this.name = channel.getName();
		this.userId = userId;
		this.userNickname = userNickname;
		this.success = true;
	}

	public static ChannelLeaveResponse success(Channel channel, UUID userId, String userNickname) {
		return new ChannelLeaveResponse(channel, userId, userNickname);
	}
}
