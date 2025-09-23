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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;

  //파일 등록
  @Override
  @Transactional
  public UUID create(BinaryContentDto dto) {
    log.info("파일 등록 시도: filename={}", dto.getFileName());
    BinaryContent file = binaryContentMapper.toEntity(dto); // dto 변환 메서드 호출
    BinaryContent saved = binaryContentRepository.save(file);
    log.info("파일 등록 완료: id={}", saved.getId());
    return saved.getId();
  }

  //파일 1개 조회
  @Override
  @Transactional
  public BinaryContentDto findById(UUID id) {
    log.info("파일 단건조회 시도: id={}", id);
    BinaryContent file = binaryContentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("파일 단건조회 실패(없음): id={}", id);
          return new IllegalArgumentException("파일을 찾을 수 없음");
        });
    log.info("파일 단건조회 성공: id={}", id);
    return binaryContentMapper.toDto(file);
  }

  //여러 파일 조회
  @Override
  @Transactional
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    log.info("여러 파일 조회 시도: 개수={}", ids.size());
    List<BinaryContent> files = binaryContentRepository.findAllByIdIn(ids);
    log.info("여러 파일 조회 완료: 반환 개수={}", files.size());
    return binaryContentMapper.toDtoList(files);
  }

  //파일 삭제
  @Override
  @Transactional
  public void deleteById(UUID id) {
    log.info("파일 삭제 시도: id={}", id);
    BinaryContent file = binaryContentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("파일 삭제 실패(없음): id={}", id);
          return new IllegalArgumentException("파일을 찾을 수 없음");
        });
    binaryContentRepository.delete(file);
    log.info("파일 삭제 완료: id={}", id);
  }
}