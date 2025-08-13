package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
    public List<Message> findByUser(User user) {
        return dataMap.values().stream()
                .filter(m -> m.getUser().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByMessage(String message) {
        return dataMap.values().stream()
                .filter(m -> m.getContent().contains(message))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        dataMap.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList()
                .forEach(message -> dataMap.remove(message.getId()));
        writeToFile();
    }
}
