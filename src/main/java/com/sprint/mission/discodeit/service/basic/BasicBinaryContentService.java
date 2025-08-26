package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.FileDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID save(FileDto dto) {
        if (dto == null) return null;

        BinaryContent bc = new BinaryContent(
                dto.name(),
                dto.contentType(),
                dto.content(),
                dto.content().length
        );

        binaryContentRepository.save(bc);
        return bc.getId();
    }

    @Override
    public BinaryContent findById(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return binaryContentRepository.findAll();
    }

    @Override
    public List<BinaryContent> findAllById(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(bc -> ids.contains(bc.getId()))
                .toList();
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
