package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {
    void save(BinaryContent image);

    void deleteByOwnerId(UUID ownerId);


    // ✅ 전체 조회 (추가 기능)
    List<BinaryContent> findAll();

    BinaryContent findById(UUID id);

    void deleteById(UUID id);
}
