package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent addBinaryContent(byte[] binaryContent) {
        BinaryContent newContent = new BinaryContent(binaryContent);
        return binaryContentRepository.save(newContent).orElseThrow();
    }

    @Override
    public BinaryContent getBinaryContentById(UUID contentId){
        return binaryContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found"));
    }

    @Override
    public List<BinaryContent> getAllBinaryContent() {
        return binaryContentRepository.findAll();
    }

    @Override
    public void deleteBinaryContent(UUID contentId){
        binaryContentRepository.deleteById(contentId);
    }
}
