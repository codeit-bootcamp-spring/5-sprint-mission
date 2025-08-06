package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;

public class FileChannelRepository extends BaseFileRepository<Channel> {
    public FileChannelRepository() {
        super(Channel.class);
    }
}
