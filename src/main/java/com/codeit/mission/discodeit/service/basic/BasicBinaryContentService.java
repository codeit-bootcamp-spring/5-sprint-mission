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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        BinaryContent binaryContent = new BinaryContent(
                fileName,
                (long) bytes.length,
                contentType
        );
        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), bytes);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException(
                        "BinaryContent with id " + binaryContentId + " not found"));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllById(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException(
                    "BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }
}
