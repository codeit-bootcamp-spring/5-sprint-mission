package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	public Message findById(UUID messageId) {
		if (messageId == null) {
			return null;
		}

		return messageMap.get(messageId);
	}

	@Override
	public List<Message> findAll() {
		return new ArrayList<>(messageMap.values());
	}

	@Override
	public List<Message> findByChannelId(UUID channelId) {
		if (channelId == null) {
			return null;
		}

		return messageMap.values().stream()
			.filter(message -> channelId.equals(message.getChannelUUID()))
			.collect(Collectors.toList());
	}

	@Override
	public void deleteById(UUID messageId) {
		if (messageId == null) {
			return;
		}

		messageMap.remove(messageId);
	}

}
