package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent> implements
    BinaryContentRepository {

  public FileBinaryContentRepository(AppProperties appProperties) {
    super(BinaryContent.class, appProperties.storage());
  }
}
