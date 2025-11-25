package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.advice.GlobalExceptionHandler;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BinaryContentController.class)
@Import(GlobalExceptionHandler.class)
class BinaryContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BinaryContentService binaryContentService;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("GET /api/binaryContents - 성공: 바이너리 콘텐츠 목록 조회")
    void findAllByIdIn_Success() throws Exception {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<BinaryContentDto> contents = List.of(
            new BinaryContentDto(id1, "file1.jpg", 1024L, "image/jpeg"),
            new BinaryContentDto(id2, "file2.pdf", 2048L, "application/pdf")
        );

        given(binaryContentService.findAllByIdIn(any())).willReturn(contents);

        // when & then
        mockMvc.perform(get("/api/binaryContents")
                .param("binaryContentIds", id1.toString(), id2.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(id1.toString()))
            .andExpect(jsonPath("$[0].fileName").value("file1.jpg"))
            .andExpect(jsonPath("$[0].size").value(1024))
            .andExpect(jsonPath("$[0].contentType").value("image/jpeg"))
            .andExpect(jsonPath("$[1].id").value(id2.toString()))
            .andExpect(jsonPath("$[1].fileName").value("file2.pdf"));

        then(binaryContentService).should().findAllByIdIn(any());
    }

    @Test
    @DisplayName("GET /api/binaryContents - 성공: 빈 목록")
    void findAllByIdIn_EmptyList() throws Exception {
        // given
        given(binaryContentService.findAllByIdIn(any())).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/binaryContents")
                .param("binaryContentIds", UUID.randomUUID().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        then(binaryContentService).should().findAllByIdIn(any());
    }

    @Test
    @DisplayName("GET /api/binaryContents/{binaryContentId} - 성공: 바이너리 콘텐츠 조회")
    void find_Success() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContentDto content = new BinaryContentDto(
            contentId,
            "test.png",
            5120L,
            "image/png"
        );

        given(binaryContentService.find(contentId)).willReturn(content);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}", contentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(contentId.toString()))
            .andExpect(jsonPath("$.fileName").value("test.png"))
            .andExpect(jsonPath("$.size").value(5120))
            .andExpect(jsonPath("$.contentType").value("image/png"));

        then(binaryContentService).should().find(contentId);
    }

    @Test
    @DisplayName("GET /api/binaryContents/{binaryContentId} - 실패: 존재하지 않는 바이너리 콘텐츠")
    void find_NotFound() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();

        given(binaryContentService.find(contentId))
            .willThrow(new BinaryContentNotFoundException());

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}", contentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("BINARY_CONTENT_NOT_FOUND"));

        then(binaryContentService).should().find(contentId);
    }

    @Test
    @DisplayName("GET /api/binaryContents/{binaryContentId}/download - 성공: 파일 다운로드")
    void download_Success() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContentDto metaData = new BinaryContentDto(
            contentId,
            "test.txt",
            17L,
            "text/plain"
        );
        byte[] fileContent = "test file content".getBytes();
        Resource resource = new ByteArrayResource(fileContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "test.txt");
        ResponseEntity<Resource> response = ResponseEntity.ok()
            .headers(headers)
            .body(resource);

        given(binaryContentService.find(contentId)).willReturn(metaData);
        given(binaryContentStorage.download(metaData)).willAnswer(invocation -> response);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", contentId))
            .andExpect(status().isOk())
            .andExpect(header().exists("Content-Disposition"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(content().bytes(fileContent));

        then(binaryContentService).should().find(contentId);
        then(binaryContentStorage).should().download(metaData);
    }

    @Test
    @DisplayName("GET /api/binaryContents/{binaryContentId}/download - 성공: 파일 다운로드 (이미지)")
    void download_Image_Success() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContentDto metaData = new BinaryContentDto(
            contentId,
            "image.jpg",
            4L,
            "image/jpeg"
        );
        byte[] imageContent = new byte[]{0x12, 0x34, 0x56, 0x78};
        Resource resource = new ByteArrayResource(imageContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "image.jpg");
        ResponseEntity<Resource> response = ResponseEntity.ok()
            .headers(headers)
            .body(resource);

        given(binaryContentService.find(contentId)).willReturn(metaData);
        given(binaryContentStorage.download(metaData)).willAnswer(invocation -> response);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", contentId))
            .andExpect(status().isOk())
            .andExpect(header().exists("Content-Disposition"))
            .andExpect(header().string("Content-Type", "image/jpeg"))
            .andExpect(content().bytes(imageContent));

        then(binaryContentService).should().find(contentId);
        then(binaryContentStorage).should().download(metaData);
    }

    @Test
    @DisplayName("GET /api/binaryContents/{binaryContentId}/download - 성공: contentType이 없을 때 기본값 적용")
    void download_NoContentType_DefaultsToOctetStream() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContentDto metaData = new BinaryContentDto(
            contentId,
            "file.bin",
            12L,
            null
        );
        byte[] fileContent = "test content".getBytes();
        Resource resource = new ByteArrayResource(fileContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "file.bin");
        ResponseEntity<Resource> response = ResponseEntity.ok()
            .headers(headers)
            .body(resource);

        given(binaryContentService.find(contentId)).willReturn(metaData);
        given(binaryContentStorage.download(metaData)).willAnswer(invocation -> response);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", contentId))
            .andExpect(status().isOk())
            .andExpect(header().exists("Content-Disposition"))
            .andExpect(header().string("Content-Type", "application/octet-stream"))
            .andExpect(content().bytes(fileContent));

        then(binaryContentService).should().find(contentId);
        then(binaryContentStorage).should().download(metaData);
    }

    @Test
    @DisplayName("GET /api/binaryContents/{binaryContentId}/download - 실패: 존재하지 않는 바이너리 콘텐츠")
    void download_NotFound() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();

        given(binaryContentService.find(contentId))
            .willThrow(new BinaryContentNotFoundException());

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", contentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("BINARY_CONTENT_NOT_FOUND"));

        then(binaryContentService).should().find(contentId);
    }
}
