package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentCreateException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryContentServiceTest {

  @Mock
  private BinaryContentRepository repository;

  @Mock
  private LocalBinaryContentStorage storage;

  @Mock
  private BinaryContentMapper mapper;

  @InjectMocks
  private BasicBinaryContentService service;

  private BinaryContent entity;
  private BinaryContentDto.CreateCommand createCommand;
  private BinaryContentDto.Detail detail;
  private byte[] bytes;

  @BeforeEach
  void setUp() {
    bytes = new byte[]{1, 2, 3};

    entity = BinaryContent.builder()
                          .fileName("file.txt")
                          .contentType("text/plain")
                          .build();

    createCommand = BinaryContentDto.CreateCommand.builder()
                                                  .fileName("file.txt")
                                                  .contentType("text/plain")
                                                  .bytes(bytes)
                                                  .build();

    detail = BinaryContentDto.Detail.builder()
                                    .fileName(entity.getFileName())
                                    .contentType(entity.getContentType())
                                    .bytes(bytes)
                                    .build();
  }

  @Test
  void create_success() {
    given(mapper.toEntity(createCommand)).willReturn(entity);
    given(repository.save(entity)).willReturn(entity);

    BinaryContent result = service.create(createCommand);

    assertThat(result).isEqualTo(entity);
    then(repository).should()
                    .save(entity);
    then(storage).should()
                 .put(result.getId(), bytes);
  }

  @Test
  void create_fail_storageException() {
    given(mapper.toEntity(createCommand)).willReturn(entity);
    given(repository.save(entity)).willReturn(entity);
    willThrow(new RuntimeException("disk full"))
        .given(storage)
        .put(any(UUID.class), eq(bytes));

    assertThrows(BinaryContentCreateException.class,
        () -> service.create(createCommand));
  }

  @Test
  void find_success() throws Exception {
    UUID id = UUID.randomUUID();
    given(repository.findById(id)).willReturn(Optional.of(entity));
    given(storage.get(id)).willReturn(new ByteArrayInputStream(bytes));
    given(mapper.toDetail(entity, bytes)).willReturn(detail);

    BinaryContentDto.Detail result = service.find(id);

    assertThat(result).isEqualTo(detail);
  }

  @Test
  void find_notFound_fail() {
    UUID id = UUID.randomUUID();
    given(repository.findById(id)).willReturn(Optional.empty());

    assertThrows(BinaryContentNotFoundException.class,
        () -> service.find(id));
  }

  @Test
  void delete_success() {
    UUID id = UUID.randomUUID();
    given(repository.findById(id)).willReturn(Optional.of(entity));

    service.delete(id);

    then(repository).should()
                    .delete(entity);
  }

  @Test
  void delete_notFound_fail() {
    UUID id = UUID.randomUUID();
    given(repository.findById(id)).willReturn(Optional.empty());

    assertThrows(BinaryContentNotFoundException.class,
        () -> service.delete(id));
  }

  @Test
  void deleteAll_success() {
    service.deleteAll();

    then(repository).should()
                    .deleteAll();
  }
}
