package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class FileMessageRepository extends AbstractFileRepository<Message> implements MessageRepository {

    public FileMessageRepository() {
        super("messages");
    }


    // TODO mission에 맞춰 구현
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return null;
    }
}
