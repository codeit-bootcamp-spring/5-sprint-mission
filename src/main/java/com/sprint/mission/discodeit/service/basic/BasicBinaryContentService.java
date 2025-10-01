package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.FindBiContentsIdInDTO;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentNotFoundException;
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
		  .orElseThrow(BinaryContentNotFoundException::new);

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
			throw new BinaryContentNotFoundException();
		}
		binaryContentStorage.put(id, null);
		binaryContentRepository.deleteById(id);
	}

	private BinaryContent DTOtoBinaryContent(CreateBiContentDTO dto) {
		long size = dto.getSize();
		String contentType = dto.getContentType();
		String filename = dto.getFileName();
		return new BinaryContent(size, contentType, filename);
	}

}
