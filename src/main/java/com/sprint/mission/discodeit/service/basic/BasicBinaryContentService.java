package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper   binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        // 1. 메타 정보만 저장
        BinaryContent meta = new BinaryContent(
            request.fileName(),
            (long) (request.bytes() != null ? request.bytes().length : 0),
            request.contentType()
        );

        BinaryContent saved = binaryContentRepository.save(meta);

        // 2. 바이너리 데이터는 따로 저장
        binaryContentStorage.put(saved.getId(), request.bytes());

        // 3. 응답으로 변환
        return binaryContentMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id));
        return binaryContentMapper.toDto(content);
    }

    // find와 findById가 중복 → 하나만 두고, 인터페이스도 정리하는 걸 권장
    @Transactional(readOnly = true)
    @Override
    public BinaryContentResponse findById(UUID id) {
        return find(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        // existsById + deleteById 대신 deleteById만 사용 가능
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent not found: " + id);
        }
        binaryContentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<?> download(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id));

        BinaryContentResponse dto = binaryContentMapper.toDto(content);
        return binaryContentStorage.download(dto);
    }

}
