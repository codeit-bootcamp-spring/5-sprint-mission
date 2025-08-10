package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent content = new BinaryContent(
                UUID.randomUUID(),
                request.userId(),
                request.data(),
                request.fileName(),
                request.contentType(),
                (long)(request.data() != null ? request.data().length : 0),
                request.messageId()
        );

        BinaryContent saved = binaryContentRepository.save(content);
        return new BinaryContentResponse(
                saved.getId(),
                saved.getMessageId(),
                saved.getFileName(),
                saved.getContentType(),
                saved.getBytes() != null ? saved.getBytes().length : 0
        );
    }

    public BinaryContentResponse find(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id));
        return new BinaryContentResponse(
                content.getId(),
                content.getMessageId(),
                content.getFileName(),
                content.getContentType(),
                content.getBytes() != null ? content.getBytes().length : 0
        );
    }

    public BinaryContentResponse findById(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id));
        return new BinaryContentResponse(
                content.getId(),
                content.getMessageId(),
                content.getFileName(),
                content.getContentType(),
                content.getBytes() != null ? content.getBytes().length : 0
        );
    }


    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(ids);
        return contents.stream()
                .map(content -> new BinaryContentResponse(
                        content.getId(),
                        content.getMessageId(),
                        content.getFileName(),
                        content.getContentType(),
                        content.getBytes() != null ? content.getBytes().length : 0
                ))
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent not found: " + id);
        }
        binaryContentRepository.deleteById(id);
    }
}
