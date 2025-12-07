package com.sprint.mission.discodeit.binarycontent.presentation;

import com.sprint.mission.discodeit.binarycontent.application.BinaryContentService;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStorage;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.error.GlobalExceptionHandler;
import com.sprint.mission.discodeit.global.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.global.security.ratelimit.LoginRateLimitFilter;
import com.sprint.mission.discodeit.support.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = BinaryContentController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            JwtAuthenticationFilter.class,
            LoginRateLimitFilter.class,
            JwtLogoutHandler.class
        }
    )
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("BinaryContentController 테스트")
class BinaryContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BinaryContentService binaryContentService;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;

    private static final UUID TEST_CONTENT_ID_1 = UUID.randomUUID();
    private static final UUID TEST_CONTENT_ID_2 = UUID.randomUUID();

    private BinaryContentDto createTestDto(UUID id, String fileName) {
        return new BinaryContentDto(
            id,
            fileName,
            1024L,
            "image/png",
            BinaryContentStatus.SUCCESS
        );
    }

    @Nested
    @DisplayName("GET /api/binaryContents")
    class FindAllById {

        @SuppressWarnings("unchecked")
        @Test
        @WithMockUser
        @DisplayName("여러 ID로 조회 시 해당 파일 목록 반환")
        void findAllById_withValidIds_returnsList() throws Exception {
            // given
            BinaryContentDto dto1 = createTestDto(TEST_CONTENT_ID_1, "file1.png");
            BinaryContentDto dto2 = createTestDto(TEST_CONTENT_ID_2, "file2.png");

            given(binaryContentService.findAllById(any(Collection.class)))
                .willReturn(List.of(dto1, dto2));

            // when & then
            mockMvc.perform(get("/api/binaryContents")
                    .param("binaryContentIds", TEST_CONTENT_ID_1.toString())
                    .param("binaryContentIds", TEST_CONTENT_ID_2.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(TEST_CONTENT_ID_1.toString()))
                .andExpect(jsonPath("$[0].fileName").value("file1.png"))
                .andExpect(jsonPath("$[1].id").value(TEST_CONTENT_ID_2.toString()))
                .andExpect(jsonPath("$[1].fileName").value("file2.png"));
        }

        @Test
        @WithMockUser
        @DisplayName("파라미터 없이 조회 시 400 반환")
        void findAllById_withoutParam_returns400() throws Exception {
            // when & then
            mockMvc.perform(get("/api/binaryContents"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("잘못된 UUID 형식으로 조회 시 400 반환")
        void findAllById_withInvalidUuid_returns400() throws Exception {
            // when & then
            mockMvc.perform(get("/api/binaryContents")
                    .param("binaryContentIds", "not-a-uuid"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("인증 없이 조회 시 403 반환")
        void findAllById_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(get("/api/binaryContents")
                    .param("binaryContentIds", TEST_CONTENT_ID_1.toString()))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/binaryContents/{binaryContentId}")
    class Find {

        @Test
        @WithMockUser
        @DisplayName("존재하는 ID로 조회 시 파일 정보 반환")
        void find_withExistingId_returnsDto() throws Exception {
            // given
            BinaryContentDto dto = createTestDto(TEST_CONTENT_ID_1, "test.png");
            given(binaryContentService.find(TEST_CONTENT_ID_1)).willReturn(dto);

            // when & then
            mockMvc.perform(get("/api/binaryContents/{binaryContentId}", TEST_CONTENT_ID_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CONTENT_ID_1.toString()))
                .andExpect(jsonPath("$.fileName").value("test.png"))
                .andExpect(jsonPath("$.size").value(1024))
                .andExpect(jsonPath("$.contentType").value("image/png"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 ID로 조회 시 404 반환")
        void find_withNonExistingId_returns404() throws Exception {
            // given
            UUID nonExistingId = UUID.randomUUID();
            given(binaryContentService.find(nonExistingId))
                .willThrow(new BinaryContentNotFoundException(nonExistingId));

            // when & then
            mockMvc.perform(get("/api/binaryContents/{binaryContentId}", nonExistingId))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("잘못된 UUID 형식으로 조회 시 400 반환")
        void find_withInvalidUuid_returns400() throws Exception {
            // when & then
            mockMvc.perform(get("/api/binaryContents/{binaryContentId}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("인증 없이 조회 시 403 반환")
        void find_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(get("/api/binaryContents/{binaryContentId}", TEST_CONTENT_ID_1))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/binaryContents/{binaryContentId}/download")
    class Download {

        @Test
        @WithMockUser
        @DisplayName("존재하는 파일 다운로드 시 리다이렉트 응답")
        void download_withExistingId_returnsRedirect() throws Exception {
            // given
            BinaryContentDto dto = createTestDto(TEST_CONTENT_ID_1, "test.png");
            String downloadUrl = "https://s3.example.com/bucket/test.png?presigned=token";

            given(binaryContentService.find(TEST_CONTENT_ID_1)).willReturn(dto);
            given(binaryContentStorage.download(dto))
                .willReturn(ResponseEntity.status(302)
                    .location(URI.create(downloadUrl))
                    .build());

            // when & then
            mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", TEST_CONTENT_ID_1))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, downloadUrl));
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 파일 다운로드 시 404 반환")
        void download_withNonExistingId_returns404() throws Exception {
            // given
            UUID nonExistingId = UUID.randomUUID();
            given(binaryContentService.find(nonExistingId))
                .willThrow(new BinaryContentNotFoundException(nonExistingId));

            // when & then
            mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", nonExistingId))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("잘못된 UUID 형식으로 다운로드 시 400 반환")
        void download_withInvalidUuid_returns400() throws Exception {
            // when & then
            mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", "not-a-uuid"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("인증 없이 다운로드 시 403 반환")
        void download_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", TEST_CONTENT_ID_1))
                .andExpect(status().isForbidden());
        }
    }
}
