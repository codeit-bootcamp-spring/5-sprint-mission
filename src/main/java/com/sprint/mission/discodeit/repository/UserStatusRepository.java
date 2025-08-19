package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.UserStatus;

public interface UserStatusRepository {
	void save(UserStatus status);
	Optional<UserStatus> findById(UUID id);
	Optional<UserStatus> findByUserId(UUID UserId);
	List<UserStatus> findByUserIdIn(List<UUID> userIdList);
	List<UserStatus> findAll();
	void deleteById(UUID id);

	void createDirectoryIfNotExists();
	void loadFile();
	void saveFile();
}
