package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JCFChannelRepository extends AbstractJCFRepository<Channel> implements
    ChannelRepository {

  @Override
  public List<Channel> findAllByUserId(UUID userId) {
    List<Channel> channels = new ArrayList<>();

    channels.addAll(data.values().stream()
        .filter(c -> c.getType().equals(ChannelType.PUBLIC))
        .toList());
    channels.addAll(data.values().stream()
        .filter(c -> c.getUserIds().contains(userId))
        .toList());

    return channels;
  }
}
