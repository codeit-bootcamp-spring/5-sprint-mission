package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentResponse create(BinaryContentCreateRequest req) {
        Objects.requireNonNull(req, "req must not be null");
        Objects.requireNonNull(req.filename(), "filename must not be null");
        Objects.requireNonNull(req.contentType(), "contentType must not be null");
        Objects.requireNonNull(req.data(), "data must not be null");

        BinaryContent saved = binaryContentRepository.save(new BinaryContent(
                req.filename(),
                req.contentType(),
                req.data()
        ));
        return toResponse(saved);
    }

    public BinaryContentResponse find(UUID id) {
        return binaryContentRepository.findById(id)
                .map(BinaryContentService::toResponse)
                .orElseThrow(() -> new java.util.NoSuchElementException("BinaryContent를 찾을 수 없습니다: " + id));
    }

    public List<BinaryContentResponse> findAll() {
        return binaryContentRepository.findAll().stream()
                .map(BinaryContentService::toResponse)
                .toList();
    }


    public List<BinaryContentResponse> findAllByIdIn(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(BinaryContentService::toResponse)
                .toList();
    }

    public boolean delete(UUID id) {
        if (id == null) return false;
        return binaryContentRepository.deleteById(id);
    }

    private static BinaryContentResponse toResponse(BinaryContent bc) {
        return new BinaryContentResponse(
                bc.getId(),
                bc.getFilename(),
                bc.getContentType(),
                bc.getSize(),
                bc.getBytes()
        );
    }
}
