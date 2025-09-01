package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.FindBiContentResult;
import com.sprint.mission.discodeit.domain.dto.FindBiContentsIdInDTO;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.response.BinaryContentResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

	private final BinaryContentRepository binaryContentRepository;

	@Override
	@Transactional
	public BinaryContent create(CreateBiContentDTO dto) {
		return binaryContentRepository.save(DTOtoBinaryContent(dto));
	}

	@Override
	@Transactional(readOnly = true)
	public FindBiContentResult find(UUID id) {

		BinaryContent binaryContent = binaryContentRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Binary content not found for ID: " + id));

		return toFindBinaryContentResult(binaryContent);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FindBiContentResult> findAllByIdIn(FindBiContentsIdInDTO dto) {
		return binaryContentRepository.findAllByIdIn(dto.getIds()).stream().map(
		  this::toFindBinaryContentResult).toList();
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		if (binaryContentRepository.existsById(id)) {
			throw new IllegalArgumentException("Binary content not found for ID: " + id);
		}
		binaryContentRepository.deleteById(id);
	}

	private BinaryContent DTOtoBinaryContent(CreateBiContentDTO dto) {
		byte[] content = dto.getContent();
		long size = dto.getSize();
		String contentType = dto.getContentType();
		String filename = dto.getFileName();
		return new BinaryContent(content, size, contentType, filename);
	}

	public FindBiContentResult toFindBinaryContentResult(BinaryContent content) {
		return FindBiContentResult.builder()
		  .createdAt(content.getCreatedAt())
		  .id(content.getId())
		  .fileName(content.getFileName())
		  .contentType(content.getContentType())
		  .bytes(content.getBytes())
		  .size(content.getSize())
		  .build();
	}

	public static BinaryContentResponse biContentResultToResponse(FindBiContentResult result) {
		return BinaryContentResponse.builder()
		  .id(result.getId())
		  .createdAt(result.getCreatedAt())
		  .fileName(result.getFileName())
		  .contentType(result.getContentType())
		  .bytes(result.getBytes())
		  .size(result.getSize())
		  .build();
	}
}
