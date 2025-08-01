package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class BinaryContent {
	private final UUID id;
	private final Instant createdAt;
	private final String filename;
	private final String type;
	private final byte[] content;
}
