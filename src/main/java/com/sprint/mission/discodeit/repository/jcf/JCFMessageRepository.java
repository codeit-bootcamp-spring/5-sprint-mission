package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JCFMessageRepository extends AbstractJCFRepository<Message> implements
    MessageRepository {

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return data.values().stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .collect(Collectors.toList());
  }
}
