package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent content = new BinaryContent(
                request.fileName(),
                (long) (request.bytes() != null ? request.bytes().length : 0),
                request.contentType(),
                request.bytes()

        );

        BinaryContent saved = binaryContentRepository.save(content);
        String base64 = content.getBytes() != null ? Base64.getEncoder().encodeToString(content.getBytes()) : "";
        return new BinaryContentResponse(
                saved.getId(),
                saved.getFileName(),
                saved.getContentType(),
                base64
        );
    }


    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id));
        String base64 = content.getBytes() != null ? Base64.getEncoder().encodeToString(content.getBytes()) : "";
        return new BinaryContentResponse(
                content.getId(),
                content.getFileName(),
                content.getContentType(),
                base64
        );
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id));
        String base64 = content.getBytes() != null ? Base64.getEncoder().encodeToString(content.getBytes()) : "";
        return new BinaryContentResponse(
                content.getId(),
                content.getFileName(),
                content.getContentType(),
                base64
        );
    }


    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(ids);

        return contents.stream()
                .map(content -> {
                    String base64 = content.getBytes() != null
                            ? Base64.getEncoder().encodeToString(content.getBytes())
                            : "";
                    return new BinaryContentResponse(
                            content.getId(),
                            content.getFileName(),
                            content.getContentType(),
                            base64
                    );
                })
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent not found: " + id);
        }
        binaryContentRepository.deleteById(id);
    }
}
