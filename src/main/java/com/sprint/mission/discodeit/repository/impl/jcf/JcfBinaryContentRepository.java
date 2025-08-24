package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfBinaryContentRepository extends AbstractJcfRepository<BinaryContent> implements
    BinaryContentRepository {

  public JcfBinaryContentRepository() {
    super(BinaryContent.class);
  }

  @Override
  public List<BinaryContent> findAllByContentType(String contentType) {
    Objects.requireNonNull(contentType, "contentType must not be null");
    return findAll().stream()
        .filter(b -> b.getContentType() != null && b.getContentType().equalsIgnoreCase(contentType))
        .toList();
  }

  @Override
  public List<BinaryContent> findAllByFilename(String filename) {
    Objects.requireNonNull(filename, "filename must not be null");
    return findAll().stream()
        .filter(b -> b.getFilename() != null && b.getFilename().equalsIgnoreCase(filename))
        .toList();
  }
}
