package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BinaryContentServiceTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentMapper binaryContentMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicBinaryContentService service;

  @Test
  void create_success() {
    byte[] bytes = "hello".getBytes();
    var req = new BinaryContentCreateRequest("a.txt", "text/plain", bytes);

    given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(
        inv -> inv.getArgument(0));
    willDoNothing().given(binaryContentStorage).put(any(UUID.class), eq(bytes));

    var id = UUID.randomUUID();
    var dto = new BinaryContentDto(id, "a.txt", (long) bytes.length, "text/plain");
    given(binaryContentMapper.toDto(any(BinaryContent.class))).willReturn(dto);

    var result = service.create(req);

    assertThat(result.fileName()).isEqualTo("a.txt");
    assertThat(result.size()).isEqualTo(bytes.length);
    assertThat(result.contentType()).isEqualTo("text/plain");

    ArgumentCaptor<BinaryContent> captor = ArgumentCaptor.forClass(BinaryContent.class);
    then(binaryContentRepository).should().save(captor.capture());
    var saved = captor.getValue();
    assertThat(saved.getFileName()).isEqualTo("a.txt");
    assertThat(saved.getSize()).isEqualTo((long) bytes.length);
    assertThat(saved.getContentType()).isEqualTo("text/plain");

    then(binaryContentStorage).should().put(any(UUID.class), eq(bytes));
  }

  @Test
  void find_success() {
    var id = UUID.randomUUID();
    var entity = new BinaryContent("a.txt", 5L, "text/plain");
    given(binaryContentRepository.findById(id)).willReturn(Optional.of(entity));

    var dto = new BinaryContentDto(id, "a.txt", 5L, "text/plain");
    given(binaryContentMapper.toDto(entity)).willReturn(dto);

    var result = service.find(id);

    assertThat(result).isEqualTo(dto);
  }

  @Test
  void find_fail_notFound() {
    var id = UUID.randomUUID();
    given(binaryContentRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.find(id)).isInstanceOf(BinaryContentNotFoundException.class);
  }

  @Test
  void findAllByIdIn_success() {
    var id1 = UUID.randomUUID();
    var id2 = UUID.randomUUID();
    var e1 = new BinaryContent("a.txt", 1L, "text/plain");
    var e2 = new BinaryContent("b.txt", 2L, "text/plain");

    given(binaryContentRepository.findAllById(List.of(id1, id2))).willReturn(List.of(e1, e2));

    var d1 = new BinaryContentDto(id1, "a.txt", 1L, "text/plain");
    var d2 = new BinaryContentDto(id2, "b.txt", 2L, "text/plain");
    given(binaryContentMapper.toDto(e1)).willReturn(d1);
    given(binaryContentMapper.toDto(e2)).willReturn(d2);

    var result = service.findAllByIdIn(List.of(id1, id2));

    assertThat(result).containsExactly(d1, d2);
  }

  @Test
  void findAllByIdIn_empty() {
    given(binaryContentRepository.findAllById(List.of())).willReturn(List.of());
    var result = service.findAllByIdIn(List.of());
    assertThat(result).isEmpty();
  }

  @Test
  void delete_success() {
    var id = UUID.randomUUID();
    given(binaryContentRepository.existsById(id)).willReturn(true);

    service.delete(id);

    then(binaryContentRepository).should().deleteById(id);
  }

  @Test
  void delete_fail_notFound() {
    var id = UUID.randomUUID();
    given(binaryContentRepository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> service.delete(id)).isInstanceOf(BinaryContentNotFoundException.class);
  }
}
