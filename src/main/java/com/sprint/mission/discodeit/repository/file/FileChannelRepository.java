package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class FileChannelRepository extends AbstractFileRepository<Channel> implements ChannelRepository {

    public FileChannelRepository() {
        super("channels");
    }

    // TODO mission 조건에 맞도록 추후 구현
    public List<Channel> findAllByUserId(UUID userId){

        return null;
    }
}
