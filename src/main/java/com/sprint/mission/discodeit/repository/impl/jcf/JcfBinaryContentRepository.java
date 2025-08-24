package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfBinaryContentRepository extends AbstractJcfRepository<BinaryContent> implements
    BinaryContentRepository {

  public JcfBinaryContentRepository() {
    super(BinaryContent.class);
  }
}
