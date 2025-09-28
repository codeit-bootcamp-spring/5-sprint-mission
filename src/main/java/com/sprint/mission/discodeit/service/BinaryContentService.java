package com.sprint.mission.discodeit.service;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
=======
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

<<<<<<< HEAD
  BinaryContentResponse create(BinaryContentCreateRequest request);

  BinaryContent find(UUID id);

  BinaryContent findById(UUID id);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);
=======
  BinaryContentDto create(BinaryContentCreateRequest request);

  BinaryContentDto find(UUID binaryContentId);

  List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

  void delete(UUID binaryContentId);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
