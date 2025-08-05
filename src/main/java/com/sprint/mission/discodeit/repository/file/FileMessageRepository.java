package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileMessageRepository extends AbstractFileRepository<Message> implements MessageRepository {

    public FileMessageRepository() {
        super("messages");
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.values().stream()
            .filter(m -> m.getChannelId().equals(channelId))
            .collect(Collectors.toList());
    }
}
