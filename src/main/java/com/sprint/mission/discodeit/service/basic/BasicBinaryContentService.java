package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BinaryContentResponse create(UserProfileImageRequest request) {
        log.info("[Service] 바이너리 컨텐츠 생성 시도");
        log.debug("[Service] 바이너리 컨텐츠 생성 요청 데이터: {}", request);
        BinaryContent binaryContent = request.toBinaryContent();

        binaryContentRepository.save(binaryContent);
        eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), request.getBytes()));

        log.info("[Service] 바이너리 컨텐츠 생성 성공");
        log.debug("[Service] 생성된 바이너리 컨텐츠: {}", binaryContent);
        return BinaryContentResponse.success(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentResponse getById(UUID id) throws IOException {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> BinaryContentNotFoundException.withBinaryContentId(id));

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
        log.info("[Service] 바이너리 컨텐츠 다운로드 시도 (DTO 생성 서비스 접근)");
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
        log.info("[Service] 바이너리 메타데이터  컨텐츠 삭제 시도");
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> BinaryContentNotFoundException.withBinaryContentId(id));

        binaryContentRepository.deleteById(id);

        log.info("[Service] 바이너리 컨텐츠 메타데이터 삭제 성공");
        return BinaryContentResponse.success(binaryContent);
    }
}