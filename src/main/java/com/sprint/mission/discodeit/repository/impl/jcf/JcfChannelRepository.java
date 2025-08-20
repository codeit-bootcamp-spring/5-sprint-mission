package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfChannelRepository extends AbstractJcfRepository<Channel> implements
    ChannelRepository {

  public JcfChannelRepository() {
    super(Channel.class);
  }
}
