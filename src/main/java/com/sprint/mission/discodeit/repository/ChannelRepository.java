package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  List<Channel> findAllByIdInOrType(Collection<UUID> ids, ChannelType channelType);

  List<Channel> findAllByType(ChannelType channelType);
}
