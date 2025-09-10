package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        String contentType = request.contentType();
        byte[] bytes = request.bytes();
        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), bytes);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID id) {
        return binaryContentMapper.toDto(binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found: " + id)));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent not found: " + id);
        }

        binaryContentRepository.deleteById(id);
    }
}
