package com.sprint.mission.discodeit.dto.response.channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChannelCreateResponse {
	private UUID id;
	private Instant createdAt;
	private String name;
	private String type; // PUBLIC or PRIVATE
	private String description;
	private boolean success;

	@Nullable
	private List<UUID> memberIds;


	private ChannelCreateResponse(Channel channel) {
		this.id = channel.getId();
		this.createdAt = channel.getCreatedAt();
		this.name = channel.getName();
		this.type = channel.getType();
		this.description = channel.getDescription();
		this.success = true;
	}

	public static ChannelCreateResponse success(Channel channel) {
		return new ChannelCreateResponse(channel);
	}

	public static ChannelCreateResponse successWithMembers(Channel channel,
														   List<UUID> memberIds) {
		ChannelCreateResponse response = new ChannelCreateResponse(channel);
		response.memberIds = memberIds;
		return response;
	}
}