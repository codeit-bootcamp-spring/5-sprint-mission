package com.sprint.mission.discodeit.service.jpa;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JpaBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        BinaryContent content = new BinaryContent(
            request.fileName(),
            (long) request.bytes().length,
            request.contentType()
        );
        BinaryContent saved = binaryContentRepository.save(content);

        // 실제 파일은 Storage에 저장
        binaryContentStorage.put(saved.getId(), request.bytes());

        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID id) {
        return binaryContentRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public void delete(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id));
        binaryContentRepository.delete(content);
        // 실제 파일 삭제는 필요시 Storage 구현체에서 처리 가능
    }

    private BinaryContentDto toDto(BinaryContent content) {
        return new BinaryContentDto(
            content.getId(),
            content.getFileName(),
            content.getSize(),
            content.getContentType()
        );
    }
}
