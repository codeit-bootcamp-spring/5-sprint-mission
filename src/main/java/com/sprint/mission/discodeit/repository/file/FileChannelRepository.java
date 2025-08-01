package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FileChannelRepository extends AbstractFileRepository<Channel> implements ChannelRepository {

    public FileChannelRepository() {
        super("channels");
    }
}
