package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;

import java.util.List;
import java.util.Optional;

public interface BinaryContentRepository extends BaseRepository<BinaryContent> {

    Optional<BinaryContent> findBySha256(String sha256);

    List<BinaryContent> findAllByContentType(String contentType);

    List<BinaryContent> findAllByFilename(String filename);
}
