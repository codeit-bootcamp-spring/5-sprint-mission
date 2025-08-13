package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FileChannelRepository extends AbstractFileRepository<Channel> implements ChannelRepository {

    public FileChannelRepository() {
        super("channels");
    }

    @Override
    public List<Channel> findByName(String channelName) {
        return dataMap.values().stream()
                .filter(channel -> channel.getChannelName().contains(channelName))
                .collect(Collectors.toList());
    }
}
