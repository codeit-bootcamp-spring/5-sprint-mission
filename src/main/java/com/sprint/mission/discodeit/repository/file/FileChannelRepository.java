package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FileChannelRepository extends FileBaseRepository<Channel> implements ChannelRepository {
    public FileChannelRepository(AppStorageProperties storageProperties) {
        super(Channel.class, storageProperties);
    }
}
