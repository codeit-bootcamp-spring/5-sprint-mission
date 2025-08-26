package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends AbstractRepository<Message> {

  List<Message> findAllByChannelId(UUID channelId);

  List<Message> findAllByAuthorId(UUID authorId);

  void deleteAllByChannelId(UUID channelId);

  void deleteAllByAuthorId(UUID authorId);

  long countByChannelId(UUID channelId);
}
