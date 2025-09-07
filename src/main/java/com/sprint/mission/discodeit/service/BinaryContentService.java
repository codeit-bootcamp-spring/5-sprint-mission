package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreateRequest request);
    BinaryContentResponse find(UUID id);
    BinaryContentResponse findById(UUID id);
    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);
    ResponseEntity<?> download(UUID id);
}
