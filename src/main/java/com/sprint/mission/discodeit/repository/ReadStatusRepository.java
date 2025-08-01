package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

public interface ReadStatusRepository {
	void save(ReadStatus status);
	Optional<ReadStatus> findById(UUID id);
	Optional<ReadStatus> findByUserId(String UserId);
	Optional<ReadStatus> findByChannelId(String ChannelId);
	List<ReadStatus> findAll();
	void deleteById(UUID id);
}
