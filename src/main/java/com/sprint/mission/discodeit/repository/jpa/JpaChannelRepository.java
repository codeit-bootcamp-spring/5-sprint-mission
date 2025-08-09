package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("prod")
public class JpaChannelRepository extends JpaBaseRepository<Channel> implements ChannelRepository {

    protected JpaChannelRepository() {
        super(Channel.class);
    }
}
