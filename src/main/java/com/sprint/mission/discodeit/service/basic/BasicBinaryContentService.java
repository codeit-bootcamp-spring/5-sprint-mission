package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.binaryContent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
	private final BinaryContentRepository binaryContentRepository;

	@Override
	public BinaryContentResponse create(CreateBinaryContentRequest request) {
		BinaryContent binaryContent = new BinaryContent(
			request.getFilename(),
			request.getContentType(),
			request.getSize(),
			request.getContent()
		);

		binaryContentRepository.save(binaryContent);

		return BinaryContentResponse.success(binaryContent);
	}

	@Override
	public BinaryContentResponse getById(UUID id) {
		BinaryContent binaryContent = binaryContentRepository.findById(id)
			.orElseThrow(BinaryContentNotFoundException::new);

		return BinaryContentResponse.success(binaryContent);
	}

	@Override
	public List<BinaryContentResponse> getAllByIdIn(List<UUID> ids) {
		List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(ids);

		return binaryContents.stream()
			.map(BinaryContentResponse::success)
			.toList();
	}

	@Override
	public BinaryContentResponse delete(UUID id) {
		BinaryContent binaryContent = binaryContentRepository.findById(id)
			.orElseThrow(BinaryContentNotFoundException::new);

		binaryContentRepository.deleteById(id);

		return BinaryContentResponse.success(binaryContent);
	}
}