package com.sprint.mission.discodeit.it;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BinaryContentApiIntegrationTest extends BaseIntegrationTest {

  @Autowired MockMvc mvc;
  @Autowired BinaryContentRepository binaryContentRepository;
  @Autowired BinaryContentStorage binaryContentStorage;

  @Test
  void find_and_download_and_findAllByIdIn() throws Exception {
    var entity = binaryContentRepository.save(new BinaryContent("a.txt", 5L, "text/plain"));
    var id = entity.getId();
    binaryContentStorage.put(id, "hello".getBytes(StandardCharsets.UTF_8));

    mvc.perform(get("/api/binaryContents/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.fileName").value("a.txt"))
        .andExpect(jsonPath("$.size").value(5));

    mvc.perform(get("/api/binaryContents").param("binaryContentIds", id.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id.toString()));

    var res = mvc.perform(get("/api/binaryContents/{id}/download", id))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
            org.hamcrest.Matchers.containsString("filename=\"a.txt\"")))
        .andReturn().getResponse();

    assertThat(res.getContentAsByteArray()).isEqualTo("hello".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void find_notFound_returns404() throws Exception {
    mvc.perform(get("/api/binaryContents/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }
}
