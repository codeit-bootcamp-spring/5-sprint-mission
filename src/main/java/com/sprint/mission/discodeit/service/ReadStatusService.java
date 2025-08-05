package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusDto.DetailResponse create(ReadStatusDto.CreateRequest request);

    ReadStatusDto.DetailResponse find(UUID id);

    List<ReadStatusDto.DetailResponse> findAllByUserId(UUID userId);

    void delete(UUID id);

    void deleteAll();
}
