package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatus {
	void save(UserStatus status);
	Optional<UserStatus> findById(UUID id);
	Optional<UserStatus> findByUserId(String UserId);
	List<UserStatus> findAll();
	void deleteById(UUID id);
}
