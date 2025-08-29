package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.Messages;

public interface MessageRepository {
	Messages save(Messages messages);

	void delete(UUID id);

	void deleteAll();

	void deleteByChannelId(UUID channelId);

	Optional<Messages> find(UUID id);

	List<Messages> findAll();

	List<Messages> findAllByChannelId(UUID channelId);

	boolean isEmpty(UUID channelId);

	Long count();
}
