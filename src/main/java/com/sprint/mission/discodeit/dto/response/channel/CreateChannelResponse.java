package com.sprint.mission.discodeit.dto.response.channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CreateChannelResponse {
	private UUID id;
	private Instant createdAt;
	private String channelName;
	private String type; // PUBLIC or PRIVATE
	private boolean success;

	@Nullable
	private List<UUID> memberIds;


	private CreateChannelResponse(Channel channel) {
		this.id = channel.getId();
		this.createdAt = channel.getCreatedAt();
		this.channelName = channel.getChannelName();
		this.type = channel.getType();
		this.success = true;
	}

	public static CreateChannelResponse success(Channel channel) {
		return new CreateChannelResponse(channel);
	}

	public static CreateChannelResponse successWithMembers(Channel channel,
														   List<UUID> memberIds) {
		CreateChannelResponse response = new CreateChannelResponse(channel);
		response.memberIds = memberIds;
		return response;
	}
}