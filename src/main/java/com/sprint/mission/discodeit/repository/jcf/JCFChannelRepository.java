package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFChannelRepository implements ChannelRepository {
	private final Map<UUID, Channel> channelMap;
	private final Map<String, UUID> channelNameToUUID;

	public JCFChannelRepository() {
		channelMap = new HashMap<>();
		channelNameToUUID = new HashMap<>();
	}

	@Override
	public void save(Channel channel) {
		if (channel == null || channel.getId() == null) {
			return;
		}

		channelMap.put(channel.getId(), channel);
		channelNameToUUID.put(channel.getChannelName(), channel.getId());
	}

	@Override
	public Optional<Channel> findById(UUID channelId) {
		return Optional.ofNullable(channelMap.get(channelId)).map(Channel::copy);
	}

	@Override
	public Optional<Channel> findByName(String channelName) {
		UUID channelId = channelNameToUUID.get(channelName);
		if (channelId == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(channelMap.get(channelId)).map(Channel::copy);
	}

	@Override
	public List<Channel> findAll() {
		List<Channel> channelList = new ArrayList<>();
		for (Channel channel : channelMap.values()) {
			channelList.add(channel.copy());
		}
		channelList.sort((c1, c2) -> c1.getChannelName().compareTo(c2.getChannelName()));
		return channelList;
	}

	@Override
	public boolean existsByName(String channelName) {
		if (channelName == null) return false;
		return channelNameToUUID.containsKey(channelName);
	}

	@Override
	public void deleteById(UUID channelId) {
		if (channelId == null) {
			throw new IllegalArgumentException("null!!");
		}

		Channel channel = channelMap.get(channelId);
		if (channel != null) {
			channelNameToUUID.remove(channel.getChannelName());
			channelMap.remove(channelId);
		}
	}

	@Override
	public void deleteByName(String channelName) {
		if (channelName == null) {
			throw new IllegalArgumentException("null!!");
		}

		UUID channelId = channelNameToUUID.get(channelName);
		if (channelId != null) {
			channelMap.remove(channelId);
			channelNameToUUID.remove(channelName);
		}
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
