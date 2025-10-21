package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentCreateException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public BinaryContent create(BinaryContentDto.CreateCommand create) {
    try {

      BinaryContent binaryContent = binaryContentRepository.save(
          binaryContentMapper.toEntity(create));

      binaryContentStorage.put(binaryContent.getId(), create.getBytes());

      return binaryContent;
    } catch (Exception e) {
      throw new BinaryContentCreateException(e.getMessage());
    }

  }

  @Override
  public BinaryContentDto.Detail find(UUID id) {
    BinaryContent binaryContent = binaryContentRepository.findById(id)
                                                         .orElseThrow(
                                                             () -> new BinaryContentNotFoundException(
                                                                 id));

    try {
      return binaryContentMapper.toDetail(binaryContent, binaryContentStorage.get(id)
                                                                             .readAllBytes());
    } catch (Exception e) {
      throw new BinaryContentCreateException(e.getMessage());
    }
  }

  @Override
  public List<BinaryContentDto.Detail> findAllByIdIn(List<UUID> ids) {
    List<BinaryContent> binaryContents = binaryContentRepository.findAllById(ids);

    return binaryContents.stream()
                         .map(bc -> {
                           try {
                             return binaryContentMapper.toDetail(bc,
                                 binaryContentStorage.get(bc.getId())
                                                     .readAllBytes());
                           } catch (Exception e) {
                             return null;
                           }
                         })
                         .toList();
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    BinaryContent binaryContent = binaryContentRepository.findById(id)
                                                         .orElseThrow(
                                                             () -> new BinaryContentNotFoundException(
                                                                 id));

    binaryContentRepository.delete(binaryContent);
  }

  @Override
  @Transactional
  public void deleteAll() {
    binaryContentRepository.deleteAll();
  }
}
