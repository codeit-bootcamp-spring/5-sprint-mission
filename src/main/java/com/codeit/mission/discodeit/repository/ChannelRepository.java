package com.codeit.mission.discodeit.repository;

import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findAllByTypeOrIdIn(ChannelType type, List<UUID> ids);
}
