package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.deventity.DevChannel;
import com.sprint.mission.discodeit.repository.devrepository.DevChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FileChannelRepository extends FileBaseRepository<DevChannel> implements DevChannelRepository {
    public FileChannelRepository(AppStorageProperties storageProperties) {
        super(DevChannel.class, storageProperties);
    }
}
