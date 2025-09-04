package com.sprint.mission.discodeit.dto.response.channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChannelResponse {
	private UUID id;
	private Instant createdAt;
	private Instant updatedAt;
	private String name;
	private String description;
	private ChannelType type; // PUBLIC or PRIVATE


	@Nullable
	private Instant lastMessageAt;


	@Nullable
	private List<UserResponse> participants;

	private ChannelResponse(Channel channel, @Nullable Instant lastMessageAt, @Nullable List<UserResponse> participants) {
		this.id = channel.getId();
		this.createdAt = channel.getCreatedAt();
		this.updatedAt = channel.getUpdatedAt();
		this.name = channel.getName();
		this.description = channel.getDescription();
		this.type = channel.getType();
		this.lastMessageAt = lastMessageAt;
		this.participants = participants;
	}

	public static ChannelResponse fromPublicChannel(Channel channel,
													@Nullable Instant lastMessageTime) {
		return new ChannelResponse(channel, lastMessageTime, null);
	}

	public static ChannelResponse fromPrivateChannel(Channel channel,
													 @Nullable Instant lastMessageTime,
													 List<UserResponse> memberIds) {
		return new ChannelResponse(channel, lastMessageTime, memberIds);
	}
}
