package com.sprint.mission.discodeit.dto.response.readStatus;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.ReadStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReadStatusResponse {
	private UUID id;
	private UUID userId;
	private UUID channelId;
	private Instant createdAt;
	private Instant updatedAt;
	private Instant lastReadAt;
	private boolean success;

	private ReadStatusResponse(ReadStatus readStatus) {
		this.id = readStatus.getId();
		this.userId = readStatus.getUser().getId();
		this.channelId = readStatus.getChannel().getId();
		this.createdAt = readStatus.getCreatedAt();
		this.updatedAt = readStatus.getUpdatedAt();
		this.lastReadAt = readStatus.getLastReadAt();
		this.success = true;
	}

	public static ReadStatusResponse success(ReadStatus readStatus) {
		return new ReadStatusResponse(readStatus);
	}
}
