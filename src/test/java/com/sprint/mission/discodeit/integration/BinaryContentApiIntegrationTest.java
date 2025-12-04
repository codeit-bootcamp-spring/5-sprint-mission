package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.infra.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
class BinaryContentApiIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("파일 다운로드 - 성공: 이미지 파일을 다운로드하고 올바른 Content-Type 반환")
    void downloadFile_Image_Success() throws Exception {
        // given - 이미지 파일 생성 및 저장
        BinaryContent binaryContent = new BinaryContent("test.jpg", 1024L, "image/jpeg");
        binaryContentRepository.save(binaryContent);

        byte[] fileData = "fake image data".getBytes();
        binaryContentStorage.put(binaryContent.getId(), fileData);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{id}/download", binaryContent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_JPEG))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("test.jpg")))
            .andExpect(content().bytes(fileData));
    }

    @Test
    @DisplayName("파일 다운로드 - 성공: PDF 파일을 다운로드하고 attachment로 반환")
    void downloadFile_Pdf_Success() throws Exception {
        // given - PDF 파일 생성 및 저장
        BinaryContent binaryContent = new BinaryContent("document.pdf", 2048L, "application/pdf");
        binaryContentRepository.save(binaryContent);

        byte[] fileData = "fake pdf data".getBytes();
        binaryContentStorage.put(binaryContent.getId(), fileData);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{id}/download", binaryContent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("document.pdf")))
            .andExpect(content().bytes(fileData));
    }

    @Test
    @DisplayName("파일 다운로드 - 성공: 텍스트 파일을 다운로드")
    void downloadFile_Text_Success() throws Exception {
        // given - 텍스트 파일 생성 및 저장
        BinaryContent binaryContent = new BinaryContent("notes.txt", 512L, "text/plain");
        binaryContentRepository.save(binaryContent);

        byte[] fileData = "Hello, World!".getBytes();
        binaryContentStorage.put(binaryContent.getId(), fileData);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{id}/download", binaryContent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("notes.txt")))
            .andExpect(content().bytes(fileData));
    }

    @Test
    @DisplayName("파일 다운로드 - 실패: 존재하지 않는 파일 다운로드 시도")
    void downloadFile_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get("/api/binaryContents/{id}/download", nonExistentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("BINARY_CONTENT_NOT_FOUND"));
    }

    @Test
    @DisplayName("파일 메타데이터 조회 - 성공: 파일 정보가 올바르게 반환됨")
    void getFileMetadata_Success() throws Exception {
        // given
        BinaryContent binaryContent = new BinaryContent("metadata.jpg", 5000L, "image/jpeg");
        binaryContentRepository.save(binaryContent);

        byte[] fileData = "image data".getBytes();
        binaryContentStorage.put(binaryContent.getId(), fileData);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{id}", binaryContent.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(binaryContent.getId().toString()))
            .andExpect(jsonPath("$.fileName").value("metadata.jpg"))
            .andExpect(jsonPath("$.size").value(5000))
            .andExpect(jsonPath("$.contentType").value("image/jpeg"));
    }

    @Test
    @DisplayName("파일 메타데이터 조회 - 실패: 존재하지 않는 파일")
    void getFileMetadata_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get("/api/binaryContents/{id}", nonExistentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("BINARY_CONTENT_NOT_FOUND"));
    }
}
