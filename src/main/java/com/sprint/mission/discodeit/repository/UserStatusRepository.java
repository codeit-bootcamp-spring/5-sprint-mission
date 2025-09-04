package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.domain.entity.UserStatus;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

	void deleteByUserId(UUID userId);

	Optional<UserStatus> findByUserId(UUID uuid);

	List<UserStatus> findByUserIdIn(List<UUID> userIds);
}
