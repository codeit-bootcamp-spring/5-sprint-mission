package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BinaryContentRepository extends BaseRepository<BinaryContent> {

    List<BinaryContent> findAllByIdIn(Set<UUID> ids);
}
