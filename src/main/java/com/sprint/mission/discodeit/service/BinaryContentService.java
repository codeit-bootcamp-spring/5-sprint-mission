package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.CreateFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(CreateFile createFile);
    Optional<BinaryContentResponse> getById(UUID id);
    List<BinaryContentResponse> getAllByIdIn(List<UUID> ids);
    boolean remove(UUID id);
}
