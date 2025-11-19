package com.sprint.mission.discodeit.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

	List<Channel> findAllByIdInOrType(Collection<UUID> ids, ChannelType channelType);
}
