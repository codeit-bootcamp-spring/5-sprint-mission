package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import java.util.List;

public interface BinaryContentRepository extends AbstractRepository<BinaryContent> {

  List<BinaryContent> findAllByContentType(String contentType);

  List<BinaryContent> findAllByFilename(String filename);
}
