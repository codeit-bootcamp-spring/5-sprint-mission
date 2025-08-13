package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.repository.BinaryContentRepository;
import com.codeit.mission.discodeit.service.BinaryContentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicBinaryContentService")
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BasicBinaryContentService(@Qualifier("binaryContentRepository") BinaryContentRepository binaryContentRepository) {
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.getFileName(),
                request.getContentType(),
                request.getSize(),
                request.getBytes(),
                null, null
        );

        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        return new BinaryContentResponse(savedBinaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found"));

        return new BinaryContentResponse(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAll();

        return binaryContents.stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .map(BinaryContentResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException("BinaryContent not found");
        }

        binaryContentRepository.deleteById(binaryContentId);
    }
}
