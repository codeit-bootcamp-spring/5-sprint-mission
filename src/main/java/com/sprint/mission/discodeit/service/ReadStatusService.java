package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;

import jakarta.validation.Valid;

public interface ReadStatusService {

	ReadStatusDto create(@Valid ReadStatusCreateRequest readStatusCreateRequest);

	ReadStatusDto findById(UUID id);

	List<ReadStatusDto> findAllByUserId(UUID userId);

	ReadStatusDto update(UUID readStatusId, @Valid ReadStatusUpdateRequest readStatusUpdateRequest);

	void delete(UUID id);
}
