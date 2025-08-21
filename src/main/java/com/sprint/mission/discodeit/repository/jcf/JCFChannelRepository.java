package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;


@Repository
@ConditionalOnProperty(
    name = "discodeit.repository.type",
    havingValue = "jcf",
    matchIfMissing = true
)
public class JCFChannelRepository implements ChannelRepository {

  Map<UUID, Channel> data = new HashMap<>();

  public JCFChannelRepository() {
  }

  @Override
  public Optional<Channel> save(Channel channel) {
    if (channel == null) {
      return Optional.empty();
    }

    data.put(channel.getId(), channel);
    return Optional.of(channel);
  }

  @Override
  public Optional<Channel> findById(UUID channelId) {
    if (data.containsKey(channelId)) {
      return Optional.of(data.get(channelId));
    }
    return Optional.empty();
  }

  @Override
  public List<Channel> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void delete(UUID channelId) {
    data.remove(channelId);
  }

  @Override
  public void deleteAll() {
    data.clear();
  }

  @Override
  public List<Channel> findPublicChannel() {
    List<Channel> resultList = new ArrayList<>();

    for (Channel channel : data.values()) {
      if (channel.getChannelType().equals(ChannelType.PUBLIC)) {
        resultList.add(channel);
      }
    }
    return resultList;
  }

}


