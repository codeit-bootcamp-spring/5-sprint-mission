package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.CreateFile;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(CreateFile createFile) {
        BinaryContent saved = new BinaryContent(createFile.fileName(), createFile.fileType(), createFile.data(), createFile.fileSize());
        binaryContentRepository.save(saved);
        return toResponse(saved);
    }

    @Override
    public Optional<BinaryContentResponse> getById(UUID id) {
        return binaryContentRepository.findById(id)
                .map(this::toResponse);
    }

    @Override
    public List<BinaryContentResponse> getAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public boolean remove(UUID id) {
        return binaryContentRepository.delete(id);
    }

    private BinaryContentResponse toResponse(BinaryContent binaryContent) {
        return new BinaryContentResponse(binaryContent.getId(), binaryContent.getFileName(), binaryContent.getFileType(), binaryContent.getData(), binaryContent.getFileSize());
    }
}
