package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreateReadStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateReadStatusDTO;
import com.sprint.mission.discodeit.domain.dto.readStatus.ReadStatusDto;

public interface ReadStatusService {
	public ReadStatusDto create(CreateReadStatusDTO dto);

	public List<ReadStatusDto> findAllByUserId(UUID userId);

	public ReadStatusDto update(UpdateReadStatusDTO dto);

	public void delete(UUID id);
}
