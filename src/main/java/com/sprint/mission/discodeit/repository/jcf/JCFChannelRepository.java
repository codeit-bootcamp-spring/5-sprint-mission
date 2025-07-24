package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

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
	public Channel findById(UUID channelId) {
		if (channelId == null) {
			return null;
		}

		return channelMap.get(channelId);
	}

	@Override
	public Channel findByName(String channelName) {
		if (channelName == null) {
			return null;
		}

		UUID channelId = channelNameToUUID.get(channelName);
		return channelId != null ? channelMap.get(channelId) : null;
	}

	@Override
	public List<Channel> findAll() {
		List<Channel> channelList = new ArrayList<>(channelMap.values());
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
}
