package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.sub.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentDto create(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        BinaryContent binaryContent = BinaryContent.builder()
                .fileName(fileName)
                .size((long) bytes.length)
                .contentType(contentType)
                .bytes(bytes)
                .build();

        return BinaryContentDto.from(binaryContentRepository.save(binaryContent));
    }

    public BinaryContentDto find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(BinaryContentDto::from)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));
    }

    public BinaryContent findEntity(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
    }

    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
                .map(BinaryContentDto::from)
                .toList();
    }

    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }
}
