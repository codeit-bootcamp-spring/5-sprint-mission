package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.neutral.NewBinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service("binaryContentService")
@RequiredArgsConstructor
@Validated
public class BasicBinaryContentService implements BinaryContentService {

	private final BinaryContentRepository binaryContentRepository;
	private final BinaryContentStorage binaryContentStorage;
	private final BinaryContentMapper binaryContentMapper;

	@Override
	@Transactional
	public BinaryContent create(@Valid NewBinaryContent newBinaryContent) {
		String fileName = newBinaryContent.fileName();
		String contentType = newBinaryContent.contentType();
		byte[] bytes = newBinaryContent.bytes();
		long size = bytes.length;

		BinaryContent binaryContent = new BinaryContent(fileName, contentType, size);
		binaryContentRepository.save(binaryContent);

		binaryContentStorage.put(binaryContent.getId(), bytes);
		return binaryContent;
	}

	@Override
	@Transactional(readOnly = true)
	public BinaryContentDto findById(UUID id) {
		return binaryContentMapper.toDto(validateId(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
		return binaryContentRepository.findAll().stream()
			.filter(binaryContent -> ids.contains(binaryContent.getId()))
			.map(binaryContentMapper::toDto)
			.toList();
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		validateId(id);
		binaryContentRepository.deleteById(id);
	}

	private BinaryContent validateId(UUID id) {
		return binaryContentRepository.findById(id)
			.orElseThrow(() -> new BinaryContentNotFoundException().addDetail("id", id));
	}
}
