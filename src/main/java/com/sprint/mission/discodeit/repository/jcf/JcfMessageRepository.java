package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfMessageRepository extends JcfBaseRepository<Message> implements MessageRepository {

    @Override
    protected String getEntityTypeName() {
        return "Message";
    }
}
