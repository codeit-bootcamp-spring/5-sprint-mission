package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.FindBiContentResult;
import com.sprint.mission.discodeit.domain.dto.FindBiContentsIdInDTO;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.response.BinaryContentResponse;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

	private final BinaryContentRepository binaryContentRepository;
	private final BinaryContentStorage binaryContentStorage;
	private final BinaryContentMapper binaryContentMapper;

	@Override
	@Transactional
	public BinaryContent create(CreateBiContentDTO dto) {
		BinaryContent newContent = binaryContentRepository.save(DTOtoBinaryContent(dto));
		binaryContentStorage.put(newContent.getId(), dto.getContent());

		return newContent;
	}

	@Override
	@Transactional(readOnly = true)
	public BinaryContentDto find(UUID id) {

		BinaryContent binaryContent = binaryContentRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Binary content not found for ID: " + id));

		return binaryContentMapper.toDto(binaryContent);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BinaryContentDto> findAllByIdIn(FindBiContentsIdInDTO dto) {
		return binaryContentRepository.findAllByIdIn(dto.getIds()).stream().map(
		  binaryContentMapper::toDto).toList();
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		if (!binaryContentRepository.existsById(id)) {
			throw new IllegalArgumentException("Binary content not found for ID: " + id);
		}
		binaryContentStorage.put(id, null);
		binaryContentRepository.deleteById(id);
	}

	private BinaryContent DTOtoBinaryContent(CreateBiContentDTO dto) {
		byte[] content = dto.getContent();
		long size = dto.getSize();
		String contentType = dto.getContentType();
		String filename = dto.getFileName();
		return new BinaryContent(size, contentType, filename);
	}

	public FindBiContentResult toFindBinaryContentResult(BinaryContent content) {
		return FindBiContentResult.builder()
		  .createdAt(content.getCreatedAt())
		  .id(content.getId())
		  .fileName(content.getFileName())
		  .contentType(content.getContentType())
		  // .bytes(content.getBytes())
		  .size(content.getSize())
		  .build();
	}

	public static BinaryContentResponse biContentResultToResponse(BinaryContentDto result) {
		return BinaryContentResponse.builder()
		  .id(result.getId())
		  .fileName(result.getFileName())
		  .contentType(result.getContentType())
		  .bytes(result.getBytes())
		  .size(result.getSize())
		  .build();
	}
}
