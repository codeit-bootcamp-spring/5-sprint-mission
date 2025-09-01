package com.sprint.mission.discodeit.service.basic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
	private final BinaryContentRepository binaryContentRepository;


	@Override
    @Transactional
	public BinaryContentResponse create(UserProfileImageRequest request) {
		BinaryContent binaryContent = new BinaryContent(
			request.getFileName(),
			request.getContentType(),
			request.getSize(),
			request.getBytes()
		);

		binaryContentRepository.save(binaryContent);

		return BinaryContentResponse.success(binaryContent);
	}

	@Override
    @Transactional(readOnly = true)
	public BinaryContentResponse getById(UUID id) {
		BinaryContent binaryContent = binaryContentRepository.findById(id)
			.orElseThrow(BinaryContentNotFoundException::new);

		return BinaryContentResponse.success(binaryContent);
	}

	@Override
    @Transactional(readOnly = true)
	public List<BinaryContentResponse> getAllByIdIn(List<UUID> ids) {
		List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(ids);

		return binaryContents.stream()
			.map(BinaryContentResponse::success)
			.toList();
	}

	@Override
    @Transactional
	public BinaryContentResponse delete(UUID id) {
		BinaryContent binaryContent = binaryContentRepository.findById(id)
			.orElseThrow(BinaryContentNotFoundException::new);

		binaryContentRepository.deleteById(id);
		return BinaryContentResponse.success(binaryContent);
	}
}