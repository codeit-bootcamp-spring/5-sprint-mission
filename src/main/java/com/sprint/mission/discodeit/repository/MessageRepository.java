package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.Message;

public interface MessageRepository {
	Message save(Message message);

	void delete(UUID id);

	void deleteAll();

	void deleteByChannelId(UUID channelId);

	Optional<Message> find(UUID id);

	List<Message> findAll();

	List<Message> findAllByChannelId(UUID channelId);

	boolean isEmpty(UUID channelId);

	Long count();
}
