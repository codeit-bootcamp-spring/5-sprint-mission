package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage storage;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    // 1) 메타만 DB에 저장 (bytes 없음)
    BinaryContent meta = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContent saved = binaryContentRepository.save(meta);

    // 2) 실제 바이트는 스토리지에 저장
    storage.put(saved.getId(), bytes);

    return saved;
  }

  @Override
  public BinaryContent find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found"));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream().toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
    }
    // 현재 Storage 인터페이스에 delete가 없으므로 메타만 삭제
    binaryContentRepository.deleteById(binaryContentId);
  }

  @Override
  public ResponseEntity<?> download(UUID binaryContentId) {
    BinaryContent meta = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found"));

    // BinaryContentDto 직접 생성(필드: id, fileName, size, contentType)
    var dto = new com.sprint.mission.discodeit.dto.data.BinaryContentDto(
        meta.getId(), meta.getFileName(), meta.getSize(), meta.getContentType()
    );
    return storage.download(dto);
  }
}