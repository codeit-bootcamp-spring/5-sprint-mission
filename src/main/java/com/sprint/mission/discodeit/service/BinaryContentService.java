package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {

    UUID save(MultipartFile file);

    List<BinaryContent> findAll();

    Optional<BinaryContent> findById(UUID id);

    BinaryContent convertMultipartFile(MultipartFile file);
}
