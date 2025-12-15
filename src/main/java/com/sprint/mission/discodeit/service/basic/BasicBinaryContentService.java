package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    log.info("파일 저장 요청: fileName={}, size={} bytes, contentType={}",
            fileName, bytes.length, contentType);

    BinaryContent binaryContent = new BinaryContent(
            fileName,
            (long) bytes.length,
            contentType
    );
    binaryContentRepository.save(binaryContent);

    eventPublisher.publishEvent(
            new BinaryContentCreatedEvent(binaryContent.getId(), bytes)
    );

    log.info("메타데이터 저장 완료. 파일 저장 이벤트 발행: id={}, fileName={}",
            binaryContent.getId(), fileName);
    return binaryContentMapper.toDto(binaryContent);
  }

  @Transactional(readOnly = true)
  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.info("파일 조회 요청: id={}", binaryContentId);
    return binaryContentRepository.findById(binaryContentId)
            .map(binaryContentMapper::toDto)
            .orElseThrow(() -> {
              log.error("파일 조회 실패 - 없음: id={}", binaryContentId);
              return new BinaryContentNotFoundException();
            });
  }

  @Transactional(readOnly = true)
  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    log.info("여러 파일 조회 요청: count={}", binaryContentIds.size());
    List<BinaryContentDto> results = binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    log.info("여러 파일 조회 완료: 요청 count={}, 성공 count={}", binaryContentIds.size(), results.size());
    return results;
  }

  /**
   * Listner 내부에서 예외 발생 시 updateStatus 중 DB 커넥션 문제가 발생하면 FAIL 업데이트마저 실패할 수 있음
   * 그러면 실제 파일은 저장 실패했는데, DB는 계속 "업로드중" 표시할 수 있음
   * 때문에, REQUIRES_NEW 를 사용하여 트랜잭션 분리되어 독립적으로 실행되게 유도
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(BinaryContentNotFoundException::new);

    binaryContent.updateStatus(status);

    return binaryContentMapper.toDto(binaryContent);
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    log.info("파일 삭제 요청: id={}", binaryContentId);
    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.error("파일 삭제 실패 - 없음: id={}", binaryContentId);
      throw new BinaryContentNotFoundException();
    }
    binaryContentRepository.deleteById(binaryContentId);
    log.info("파일 삭제 성공: id={}", binaryContentId);
  }
}