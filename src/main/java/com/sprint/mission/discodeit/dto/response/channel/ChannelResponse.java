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
public class ChannelResponse {
	private UUID id;
	private Instant createdAt;
	private Instant updatedAt;
	private String channelName;
	private String type; // PUBLIC or PRIVATE

	@Nullable
	private Instant lastMessageTime;

	@Nullable
	private List<UUID> memberIds;

	private ChannelResponse(Channel channel, @Nullable Instant lastMessageTime, @Nullable List<UUID> participantIds) {
		this.id = channel.getId();
		this.createdAt = channel.getCreatedAt();
		this.updatedAt = channel.getUpdatedAt();
		this.channelName = channel.getChannelName();
		this.type = channel.getType();
		this.lastMessageTime = lastMessageTime;
		this.memberIds = participantIds;
	}

	public static ChannelResponse fromPublicChannel(Channel channel,
													@Nullable Instant lastMessageTime) {
		return new ChannelResponse(channel, lastMessageTime, null);
	}

	public static ChannelResponse fromPrivateChannel(Channel channel,
													 @Nullable Instant lastMessageTime,
													 List<UUID> memberIds) {
		return new ChannelResponse(channel, lastMessageTime, memberIds);
	}
}
