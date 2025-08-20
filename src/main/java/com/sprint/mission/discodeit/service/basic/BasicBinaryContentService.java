package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                request.getFilename(),
                request.getContentType(),
                request.getSize(),
                request.getData()
        );
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
        return toBinaryContentResponse(savedBinaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
        return toBinaryContentResponse(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(this::toBinaryContentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent with id " + id + " not found");
        }
        binaryContentRepository.deleteById(id);
    }

    @Override
    public void clear() {
        binaryContentRepository.clear();
    }

    private BinaryContentResponse toBinaryContentResponse(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getFilename(),
                binaryContent.getContentType(),
                binaryContent.getSize(),
                binaryContent.getData(),
                binaryContent.getCreatedAt()
        );
    }
}