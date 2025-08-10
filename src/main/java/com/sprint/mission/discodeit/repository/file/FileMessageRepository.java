package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FileMessageRepository extends FileBaseRepository<Message> implements MessageRepository {
    public FileMessageRepository(AppStorageProperties storageProperties) {
        super(Message.class, storageProperties);
    }
}