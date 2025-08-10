package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent content = new BinaryContent(
                request.getFileName(),
                request.getContentType(),
                request.getSize(),
                request.getBytes(),
                request.getUserId(),
                request.getMessageId()
        );

        binaryContentRepository.save(content);
        return toResponse(content);

    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("BinaryContent를 찾을 수 없습니다."));
        return toResponse(content);
    }

    @Override
    public List<BinaryContentResponse> findAllByUserId(List<UUID> ids) {
        return ids.stream()
                .map(binaryContentRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent를 찾을 수 없습니다.");
        }
        binaryContentRepository.deleteById(id);
    }

    private BinaryContentResponse toResponse(BinaryContent content) {
        return new BinaryContentResponse(
                content.getId(),
                content.getCreatedAt(),
                content.getFileName(),
                content.getContentType(),
                content.getSize(),
                content.getUserId(),
                content.getMessageId() // messageId로 받아도 ActiveId로 저장됨
        );
    }

}
