package com.sprint.mission.discodeit.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageQueryRepository {
	Slice<Message> search(UUID channelId, Instant createdAt, Pageable pageable);

	Optional<Message> findLastMessage(UUID channelId);
}
