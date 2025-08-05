package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;
import java.util.UUID;

public class JCFChannelRepository extends AbstractJCFRepository<Channel> implements ChannelRepository {
    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return List.of();
    }
}
