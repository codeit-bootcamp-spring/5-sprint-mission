package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

  //첨부파일 저장
  void save(BinaryContent image);

  //첨부파일 한개만 조회
  BinaryContent findById(UUID id);

  //모든 첨부파일 리스트로 반환
  List<BinaryContent> findAll();

  //여러 UUID 받아서 여러 파일 조회
  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  //첨부파일 한개만 삭제
  void deleteById(UUID id);
}
