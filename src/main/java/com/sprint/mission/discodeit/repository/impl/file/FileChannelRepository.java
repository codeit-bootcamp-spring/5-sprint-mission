package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FileChannelRepository extends AbstractFileRepository<Channel> implements
    ChannelRepository {

  public FileChannelRepository(AppProperties appProperties) {
    super(Channel.class, appProperties.storage());
  }

  @Override
  protected String getEntityTypeName() {
    return "채널";
  }

}
