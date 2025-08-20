package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileChannelRepository extends AbstractFileRepository<Channel> implements
    ChannelRepository {

  public FileChannelRepository() {
    super("data.dir", "channels");
  }

  public FileChannelRepository(String basePath) {
    super(basePath, "channels");
  }

  public List<Channel> findAllByUserId(UUID userId) {

    List<Channel> channels = new ArrayList<>();

    channels.addAll(
        data.values().stream().filter(c -> c.getType().equals(ChannelType.PUBLIC)).toList());
    channels.addAll(data.values().stream().filter(c -> c.getType().equals(ChannelType.PRIVATE))
        .filter(c -> c.getUserIds().contains(userId)).toList());

    return channels;
  }
}
