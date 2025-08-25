package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileMessageRepository extends AbstractFileRepository<Message> implements
    MessageRepository {

  public FileMessageRepository() {
    super("data.dir", "messages");
  }

  public FileMessageRepository(String basePath) {
    super(basePath, "messages");
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return data.values()
               .stream()
               .filter(m -> m.getChannelId()
                             .equals(channelId))
               .toList();
  }
}
