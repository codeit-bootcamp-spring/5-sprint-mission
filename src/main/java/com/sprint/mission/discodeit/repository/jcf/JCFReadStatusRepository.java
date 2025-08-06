package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.UUID;

public class JCFReadStatusRepository implements ReadStatusRepository {

    @Override
    public void save(ReadStatus readStatus) {

    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return null;
    }
}
