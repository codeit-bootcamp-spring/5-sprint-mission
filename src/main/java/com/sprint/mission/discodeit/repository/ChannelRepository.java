package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelRepository {
	void save(Channel channel);
	Optional<Channel> findById(UUID channelId);
	Optional<Channel> findByName(String channelName);
	List<Channel> findAll();
	boolean existsByName(String channelName);
	void deleteById(UUID channelId);
	void deleteByName(String channelName);

	void createDirectoryIfNotExists();
	void loadFile();
	void saveFile();

}
