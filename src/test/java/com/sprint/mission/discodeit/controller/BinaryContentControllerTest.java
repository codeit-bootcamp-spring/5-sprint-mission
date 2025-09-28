package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BinaryContentController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class BinaryContentControllerTest {

  @Autowired MockMvc mvc;

  @SuppressWarnings({"removal","deprecation"})
  @org.springframework.boot.test.mock.mockito.MockBean
  BinaryContentService binaryContentService;

  @SuppressWarnings({"removal","deprecation"})
  @org.springframework.boot.test.mock.mockito.MockBean
  BinaryContentStorage binaryContentStorage;

  @Test
  void find_success() throws Exception {
    var id = UUID.randomUUID();
    var dto = new BinaryContentDto(id, "a.txt", 5L, "text/plain");
    given(binaryContentService.find(eq(id))).willReturn(dto);

    mvc.perform(get("/api/binaryContents/{binaryContentId}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.fileName").value("a.txt"))
        .andExpect(jsonPath("$.size").value(5));
  }

  @Test
  void find_fail_notFound() throws Exception {
    var id = UUID.randomUUID();
    willThrow(new BinaryContentNotFoundException(id)).given(binaryContentService).find(eq(id));

    mvc.perform(get("/api/binaryContents/{binaryContentId}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("BINARY_CONTENT_NOT_FOUND"));
  }

  @Test
  void findAllByIdIn_success() throws Exception {
    var id1 = UUID.randomUUID();
    var id2 = UUID.randomUUID();
    var d1 = new BinaryContentDto(id1, "a.txt", 5L, "text/plain");
    var d2 = new BinaryContentDto(id2, "b.png", 12L, "image/png");

    given(binaryContentService.findAllByIdIn(eq(List.of(id1, id2))))
        .willReturn(List.of(d1, d2));

    mvc.perform(get("/api/binaryContents")
            .param("binaryContentIds", id1.toString(), id2.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id1.toString()))
        .andExpect(jsonPath("$[1].id").value(id2.toString()));
  }

  @Test
  void download_success() throws Exception {
    var id  = UUID.randomUUID();
    var dto = new BinaryContentDto(id, "a.txt", 5L, "text/plain");

    given(binaryContentService.find(eq(id))).willReturn(dto);

    var bytes = "hello".getBytes();

    ResponseEntity<Resource> resp = ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"a.txt\"")
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(bytes.length)
        .body(new ByteArrayResource(bytes));

    given(binaryContentStorage.download(eq(dto)))
        .willReturn((ResponseEntity) resp);

    mvc.perform(get("/api/binaryContents/{binaryContentId}/download", id))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"a.txt\""))
        .andExpect(content().bytes(bytes));
  }
}
