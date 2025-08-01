package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.List;
import java.util.UUID;

public class JCFMessageRepository extends AbstractJCFRepository<Message> implements MessageRepository {

    // TODO mission에 맞춰 구현
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return null;
    }
}
