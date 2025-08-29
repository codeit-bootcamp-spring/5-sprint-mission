package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "jcf",
  matchIfMissing = true // 값이 없으면 JCF로 등록
)
public class JCFMessageRepository implements MessageRepository {

	public final Map<UUID, Message> data;

	public JCFMessageRepository() {
		this.data = new HashMap<>();
	}

	@Override
	public Message save(Message message) {
		data.put(message.getId(), message);
		return message;
	}

	@Override
	public void delete(UUID id) {
		if (!data.containsKey(id)) {
			throw new IllegalArgumentException("Message with ID " + id + " not found");
		}
		data.remove(id);
	}

	@Override
	public void deleteAll() {
		if (data.isEmpty()) {
			return;
			// throw new IllegalArgumentException("No messages to delete");
		}
		data.clear();
	}

	@Override
	public void deleteByChannelId(UUID channelId) {
		data.values().removeIf(message -> message.getChannel().getId().equals(channelId));
	}

	@Override
	public Optional<Message> find(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public List<Message> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public List<Message> findAllByChannelId(UUID channelId) {
		return data.values().stream()
		  .filter(message -> message.getChannel().getId().equals(channelId))
		  .toList();
	}

	public boolean isEmpty(UUID id) {
		return !data.containsKey(id);
	}

	@Override
	public Long count() {
		return (long)data.size();
	}
}
