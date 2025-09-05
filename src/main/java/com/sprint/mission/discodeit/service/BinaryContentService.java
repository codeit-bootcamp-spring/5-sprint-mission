package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  //파일 저장
  UUID create(BinaryContentCreateRequest request);

  //파일 단건 조회
  BinaryContent findById(UUID id);

  //여러 UUID 받아서 여러 파일 조회 (예: 파일 여러개 미리보기할때)
  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  //파일 한개 삭제
  void deleteById(UUID id);
}
