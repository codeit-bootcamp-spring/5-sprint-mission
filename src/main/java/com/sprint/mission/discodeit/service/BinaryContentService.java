package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContent create(@Valid BinaryContentCreateRequest request) {
        Path path = request.path();
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new ThrowableIOException("BinaryContent를 읽어오지 못했습니다.", e);
        }

        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), bytes);
        if (!Arrays.stream(request.fileName().split("\\."))
                .collect(Collectors.toCollection(LinkedList::new))
                .getLast()
                .equals(request.contentType())) {
            throw new IllegalArgumentException("create : 잘못된 확장자입니다.");
        }
        return binaryContentRepository.save(binaryContent);
    }

    public BinaryContent findById(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("findById : BinaryContent를 찾을 수 없습니다."));
    }

    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("delete : BinaryContent를 찾을 수 없습니다.");
        }
        binaryContentRepository.deleteById(id);
    }

    public void deleteAll() {
        binaryContentRepository.findAll().forEach(binaryContent -> delete(binaryContent.getId()));
    }
}
