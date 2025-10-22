package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.mapper.BinaryContentMapper;
import com.codeit.mission.discodeit.repository.BinaryContentRepository;
import com.codeit.mission.discodeit.service.BinaryContentService;
import com.codeit.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        log.info("파일 업로드 요청 - fileName: {}, contentType: {}", fileName, contentType);

        BinaryContent binaryContent = new BinaryContent(
                fileName,
                (long) bytes.length,
                contentType
        );
        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), bytes);

        log.info("파일 업로드 완료 - binaryContentId: {}, fileName: {}", binaryContent.getId(), fileName);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        log.debug("파일 정보 조회 요청 - binaryContentId: {}", binaryContentId);

        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException(
                        "BinaryContent with id " + binaryContentId + " not found"));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.debug("파일 정보 일괄 조회 요청 - 파일 수: {}", binaryContentIds.size());
        log.debug("조회 대상 binaryContentId 목록: {}", binaryContentIds);

        return binaryContentRepository.findAllById(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        log.info("파일 삭제 요청 - binaryContentId: {}", binaryContentId);

        if (!binaryContentRepository.existsById(binaryContentId)) {
            log.warn("파일 삭제 실패 - 존재하지 않는 binaryContentId: {}", binaryContentId);
            throw new NoSuchElementException(
                    "BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }
}
