package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelRepository {
	void save(Channel channel);
	Channel findById(UUID channelId);
	Channel findByName(String channelName);
	List<Channel> findAll();
	boolean existsByName(String channelName);
	void deleteById(UUID channelId);
	void deleteByName(String channelName);
}
