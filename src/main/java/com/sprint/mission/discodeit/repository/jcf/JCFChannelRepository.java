package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

@Repository
@ConditionalOnProperty(
  prefix = "discodeit.repository",
  name = "type",
  havingValue = "jcf",
  matchIfMissing = true // 값이 없으면 JCF로 등록
)
public class JCFChannelRepository implements ChannelRepository {

	public final Map<UUID, Channel> data;

	public JCFChannelRepository() {
		this.data = new HashMap<>();
	}

	@Override
	public Channel save(Channel channel) {
		data.put(channel.getId(), channel);
		return channel;
	}

	@Override
	public Optional<Channel> find(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public List<Channel> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public void delete(UUID id) {
		if (!data.containsKey(id)) {
			throw new IllegalArgumentException("Channel with ID " + id + " not found");
		}
		data.remove(id);
	}

	@Override
	public boolean existsById(UUID id) {
		return data.get(id) != null;
	}

	@Override
	public void deleteAll() {
		data.clear();
	}

	@Override
	public Long count() {
		return (long)data.size();
	}
}
