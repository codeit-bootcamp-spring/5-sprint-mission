package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entitydev.DevChannel;
import com.sprint.mission.discodeit.repository.devrepository.DevChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfChannelRepository extends JcfBaseRepository<DevChannel> implements DevChannelRepository {

    @Override
    protected String getEntityTypeName() {
        return "Channel";
    }
}
