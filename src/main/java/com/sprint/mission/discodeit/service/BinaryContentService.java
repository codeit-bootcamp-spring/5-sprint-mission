package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.NewBinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;

import jakarta.validation.Valid;

public interface BinaryContentService {

	BinaryContent create(@Valid NewBinaryContent newBinaryContent);

	BinaryContentDto findById(UUID id);

	List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

	void delete(UUID id);

	BinaryContentDto updateStatus(UUID id, BinaryContentStatus status);
}
