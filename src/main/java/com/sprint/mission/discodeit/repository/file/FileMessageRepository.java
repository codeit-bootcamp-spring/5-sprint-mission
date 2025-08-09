package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.deventity.DevMessage;
import com.sprint.mission.discodeit.repository.devrepository.DevMessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FileMessageRepository extends FileBaseRepository<DevMessage> implements DevMessageRepository {
    public FileMessageRepository(AppStorageProperties storageProperties) {
        super(DevMessage.class, storageProperties);
    }
}