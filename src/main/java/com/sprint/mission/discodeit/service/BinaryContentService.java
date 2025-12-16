package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContent createBinaryContent(MultipartFile profile);

  BinaryContentDTO updateStatus(UUID binaryContentId, BinaryContentStatus status);

  BinaryContentDTO findByBinaryContentId(UUID binaryContentId);

  List<BinaryContentDTO> findAllByIdIn(List<UUID> attachmentIds);

  void delete(UUID binaryContentId);

  void deleteAll(List<BinaryContent> attachmentIds);
}
