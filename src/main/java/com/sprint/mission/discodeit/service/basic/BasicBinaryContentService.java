package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto.Create;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContentDto.DetailResponse create(Create request) {
    BinaryContent binaryContent = binaryContentRepository.save(BinaryContent.of(request.getFile()));
    binaryContentRepository.save(binaryContent);

    return BinaryContentDto.DetailResponse.builder()
        .id(binaryContent.getId())
        .bytes(binaryContent.getContent())
        .contentType(binaryContent.getContentType())
        .createdAt(binaryContent.getCreatedAt())
        .name(binaryContent.getFileName())
        .size(binaryContent.getFileSize())
        .build();
  }

  @Override
  public BinaryContentDto.DetailResponse find(UUID id) {
    BinaryContent binaryContent = binaryContentRepository.findById(id).orElse(null);
    if (binaryContent == null) {
      return null;
    }

    return BinaryContentDto.DetailResponse.builder()
        .id(binaryContent.getId())
        .bytes(binaryContent.getContent())
        .contentType(binaryContent.getContentType())
        .createdAt(binaryContent.getCreatedAt())
        .name(binaryContent.getFileName())
        .size(binaryContent.getFileSize())
        .build();
  }

  @Override
  public List<BinaryContentDto.DetailResponse> findAllByIdIn(List<UUID> ids) {
    List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(ids);

    return binaryContents.stream()
        .map(bc -> BinaryContentDto.DetailResponse.builder()
            .id(bc.getId())
            .bytes(bc.getContent())
            .contentType(bc.getContentType())
            .createdAt(bc.getCreatedAt())
            .name(bc.getFileName())
            .size(bc.getFileSize())
            .build())
        .toList();
  }

  @Override
  public void delete(UUID id) {
    binaryContentRepository.delete(id);
  }

  @Override
  public void deleteAll() {
    binaryContentRepository.deleteAll();
  }
}
