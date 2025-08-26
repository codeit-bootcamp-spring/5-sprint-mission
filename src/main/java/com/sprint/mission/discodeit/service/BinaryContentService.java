package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.FileDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    UUID save(FileDto dto);

    List<BinaryContent> findAll();

    List<BinaryContent> findAllById(List<UUID> ids);

    BinaryContent findById(UUID id);

    BinaryContent convertMultipartFile(MultipartFile file);
}
