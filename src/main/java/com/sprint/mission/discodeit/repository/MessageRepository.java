package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageRepository {
	void save(Message message);
	Optional<Message> findById(UUID messageId);
	List<Message> findAll();
	List<Message> findByChannelId(UUID channelId);
	// List<Message> findByAuthorId(UUID authorId);
	// List<Message> findByChannelIdAndAuthorId(UUID channelId, UUID authorId);
	void deleteById(UUID messageId);
}
