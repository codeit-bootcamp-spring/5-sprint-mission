package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.domain.entity.ReadStatus;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

	public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

	public List<ReadStatus> findAllByUserId(UUID userId);

	public List<ReadStatus> findAllByChannelId(UUID channelId);

	public void deleteByChannelId(UUID channelId);

}
