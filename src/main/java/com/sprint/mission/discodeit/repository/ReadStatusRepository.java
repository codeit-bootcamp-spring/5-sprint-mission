package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

public interface ReadStatusRepository {
	void save(ReadStatus status);
	Optional<ReadStatus> findById(UUID id);
	List<ReadStatus> findByUserId(UUID UserId);
	List<ReadStatus> findByChannelId(UUID ChannelId);
	List<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId);
	List<ReadStatus> findAll();
	void deleteById(UUID id);
	void deleteByChannelId(UUID channelId);
	void deleteByUserIdAndChannelId(UUID userId, UUID channelId);
	boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);

	void createDirectoryIfNotExists();
	void loadFile();
	void saveFile();
}
