package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.respository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public void save(BinaryContent binaryContent) {
        binaryContentRepository.save(binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return binaryContentRepository.findById(id);
    }

    @Override
    public List<BinaryContent> findAll() {
        return binaryContentRepository.findAll();
    }

    @Override
    public BinaryContent convertMultipartFile(MultipartFile file) {
        try {
            return new BinaryContent(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("업로드한 파일을 처리할 수 없습니다.", e);
        }
    }
}
