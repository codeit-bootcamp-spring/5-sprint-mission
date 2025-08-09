package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("prod")
public class JpaMessageRepository extends JpaBaseRepository<Message> implements MessageRepository {

    protected JpaMessageRepository() {
        super(Message.class);
    }
}
