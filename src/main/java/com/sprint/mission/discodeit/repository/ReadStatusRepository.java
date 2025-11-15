package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.entity.ReadStatus;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

	List<ReadStatus> findAllByUserId(UUID userId);

	@EntityGraph(attributePaths = {"user", "channel"})
	List<ReadStatus> findAllByChannelId(UUID channelId);
}
