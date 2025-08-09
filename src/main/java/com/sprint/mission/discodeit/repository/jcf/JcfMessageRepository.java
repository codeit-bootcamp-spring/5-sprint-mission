package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entitydev.DevMessage;
import com.sprint.mission.discodeit.repository.devrepository.DevMessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfMessageRepository extends JcfBaseRepository<DevMessage> implements DevMessageRepository {

    @Override
    protected String getEntityTypeName() {
        return "Message";
    }
}
