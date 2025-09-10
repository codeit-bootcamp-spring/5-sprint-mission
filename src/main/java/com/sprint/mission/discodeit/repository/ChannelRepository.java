package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.main.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
}
