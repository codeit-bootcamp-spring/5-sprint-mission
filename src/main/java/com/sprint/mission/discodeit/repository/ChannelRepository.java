package com.sprint.mission.discodeit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.domain.entity.Channel;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
}
