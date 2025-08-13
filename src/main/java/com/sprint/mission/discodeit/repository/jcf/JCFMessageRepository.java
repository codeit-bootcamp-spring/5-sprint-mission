package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFMessageRepository implements MessageRepository {
	private final Map<UUID, Message> messageMap;

	public JCFMessageRepository() {
		messageMap = new ConcurrentHashMap<>();
	}

	@Override
	public void save(Message message) {
		if (message == null || message.getId() == null) {
			return;
		}

		messageMap.put(message.getId(), message);
	}

	@Override
	public Optional<Message> findById(UUID messageId) {
		return Optional.ofNullable(messageMap.get(messageId)).map(Message::copy);
	}

	@Override
	public List<Message> findAll() {
		List<Message> messages = new ArrayList<>();
		for (Message message : messageMap.values()) {
			messages.add(message.copy());
		}
		return messages;
	}

	@Override
	public List<Message> findByChannelId(UUID channelId) {
		if (channelId == null) {
			return new ArrayList<>();
		}

		List<Message> messages = new ArrayList<>();
		for (Message message : messageMap.values()) {
			if (channelId.equals(message.getChannelId())) {
				messages.add(message.copy());
			}
		}
		return messages;
	}

	@Override
	public void deleteById(UUID messageId) {
		if (messageId == null) {
			return;
		}

		messageMap.remove(messageId);
	}

	@Override
	public void deleteByChannelId(UUID channelId) {
		System.out.println("미구현");
	}

	@Override
	public List<Message> findByAuthorIdAndChannelId(UUID authorId, UUID channelId) {
		return List.of();
	}

	@Override
	public void createDirectoryIfNotExists() {

	}

	@Override
	public void loadFile() {

	}

	@Override
	public void saveFile() {

	}

}
