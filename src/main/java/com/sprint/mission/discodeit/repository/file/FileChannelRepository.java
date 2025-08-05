package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileChannelRepository extends AbstractFileRepository<Channel> implements ChannelRepository {

    public FileChannelRepository() {
        super("channels");
    }

    public List<Channel> findAllByUserId(UUID userId){

        return data.values().stream()
            .filter(c -> c.getUserIds().contains(userId))
            .collect(Collectors.toList());
    }
}
