package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

public class FileMessageRepository extends BaseFileRepository<Message> implements MessageRepository {
    public FileMessageRepository() {
        super(Message.class);
    }
}