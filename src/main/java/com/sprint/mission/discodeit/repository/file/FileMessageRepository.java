package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

public class FileMessageRepository extends AbstractFileRepository<Message> implements MessageRepository {

    public FileMessageRepository() {
        super("messages");
    }

    @Override
    public List<Message> findByUser(User user) {
        return dataList.stream()
                .filter(m -> m.getUser().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByMessage(String message) {
        return dataList.stream()
                .filter(m -> m.getMessage().contains(message))
                .collect(Collectors.toList());
    }
}
