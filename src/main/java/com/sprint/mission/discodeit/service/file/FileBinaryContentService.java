package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID create(BinaryContentCreateRequest request) {
        UUID newId = UUID.randomUUID();

        BinaryContent binaryContent = new BinaryContent(
                newId,
                Instant.now(),                   // createdAt
                request.getOwnerId(),            // ownerId
                request.getFileName(),           // fileName
                request.getContentType(),        // contentType
                request.getSize(),
                request.getData()
        );
        binaryContentRepository.save(binaryContent);
        return newId; // 새로 생성한 파일의 ID 반환
    }

    @Override
    public BinaryContent findById(UUID id) {
        BinaryContent found = binaryContentRepository.findById(id);
        if (found == null) {
            throw new IllegalArgumentException("해당 ID의 BinaryContent가 없습니다.");
        }
        return found;
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(b -> ids.contains(b.getId()))
                .toList(); // 오타 수정 (toLisst ❌ → toList ✅)
    }

    @Override
    public void deleteById(UUID id) {
        BinaryContent existing = binaryContentRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("삭제할 BinaryContent가 존재하지 않습니다.");
        }
        binaryContentRepository.deleteById(id); // ✅ 조건 밖에서 호출

    }
}

