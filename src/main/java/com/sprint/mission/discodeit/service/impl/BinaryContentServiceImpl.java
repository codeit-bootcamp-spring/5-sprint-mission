package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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
  private final BinaryContentMapper binaryContentMapper;

  //파일 등록
  @Override
  @Transactional
  public UUID create(BinaryContentDto dto) {
    BinaryContent file = binaryContentMapper.toEntity(dto); // dto 변환 메서드 호출
    BinaryContent saved = binaryContentRepository.save(file);
    return saved.getId();
  }

  //파일 1개 조회
  @Override
  @Transactional
  public BinaryContentDto findById(UUID id) {
    BinaryContent file = binaryContentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없음"));
    return binaryContentMapper.toDto(file);
  }

  //여러 파일 조회
  @Override
  @Transactional
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    List<BinaryContent> files = binaryContentRepository.findAllByIdIn(ids);
    return binaryContentMapper.toDtoList(files);
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
