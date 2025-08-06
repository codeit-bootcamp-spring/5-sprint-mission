package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    void create(BinaryContent binaryContent);
    BinaryContent findById(UUID id);
    List<BinaryContent> findAll(List<UUID> ids);
    void deleteById(UUID id);
}
