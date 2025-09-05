package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  //파일 등록
  @Override
  @Transactional
  public UUID create(BinaryContentCreateRequest request) {
    BinaryContent file = new BinaryContent(
        request.getFileName(),
        request.getContentType(),
        request.getSize(),
        request.getData()
    );
    // 영속상태 등록
    BinaryContent saved = binaryContentRepository.save(file);
    return saved.getId(); // PK UUID id 반환 <- 다른 곳에서 파일 조회&식별 가능
  }

  //파일 1개 조회
  @Override
  @Transactional
  public BinaryContent findById(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없음"));
  }


  //여러 파일 조회
  @Override
  @Transactional
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllByIdIn(ids);
  }

  //파일 삭제
  @Override
  @Transactional
  public void deleteById(UUID id) {
    BinaryContent file = binaryContentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없음"));
    binaryContentRepository.delete(file);
  }
}
