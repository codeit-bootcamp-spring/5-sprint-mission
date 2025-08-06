package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

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
			if (channelId.equals(message.getChannelUUID())) {
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

}
