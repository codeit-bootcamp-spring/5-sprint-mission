package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
	private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;


	@Override
    @Transactional
	public BinaryContentResponse create(UserProfileImageRequest request) {
        log.info("바이너리 컨텐츠 생성 시도");
        log.debug("바이너리 컨텐츠 생성 요청 데이터: {}", request);
		BinaryContent binaryContent = request.toBinaryContent();

		binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), request.getBytes());

        log.info("바이너리 컨텐츠 생성 성공");
        log.debug("생성된 바이너리 컨텐츠: {}", binaryContent);
		return BinaryContentResponse.success(binaryContent);
	}

	@Override
    @Transactional(readOnly = true)
	public BinaryContentResponse getById(UUID id) throws IOException {
		BinaryContent binaryContent = binaryContentRepository.findById(id)
			.orElseThrow(BinaryContentNotFoundException::new);

        BinaryContentResponse response = BinaryContentResponse.success(binaryContent);
        try (InputStream inputStream = binaryContentStorage.get(id)) {
            response.setBytes(inputStream.readAllBytes());
        }

		return response;
	}

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentResponse> getAllByIdIn(List<UUID> ids) throws IOException {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(ids);
        List<BinaryContentResponse> responses = new ArrayList<>();

        for (BinaryContent binaryContent : binaryContents) {
            BinaryContentResponse response = BinaryContentResponse.success(binaryContent);

            try (InputStream inputStream = binaryContentStorage.get(binaryContent.getId())) {
                response.setBytes(inputStream.readAllBytes());
            }

            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDTO download(UUID id) throws IOException {
        BinaryContentResponse response = getById(id);

        return BinaryContentDTO.builder()
                .id(response.getId())
                .fileName(response.getFileName())
                .contentType(response.getContentType())
                .size(response.getSize())
                .build();
    }

    @Override
    @Transactional
	public BinaryContentResponse delete(UUID id) {
        log.info("바이너리 컨텐츠 삭제 시도");
		BinaryContent binaryContent = binaryContentRepository.findById(id)
			.orElseThrow(BinaryContentNotFoundException::new);

		binaryContentRepository.deleteById(id);

        log.info("바이너리 컨텐츠 삭제 성공");
		return BinaryContentResponse.success(binaryContent);
	}
}