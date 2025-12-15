package com.sprint.mission.discodeit.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;
import com.sprint.mission.discodeit.global.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.global.security.ratelimit.LoginRateLimitFilter;
import com.sprint.mission.discodeit.support.TestSecurityConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = GlobalExceptionHandlerTest.TestController.class,
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
@WithMockUser
@DisplayName("GlobalExceptionHandler 단위 테스트")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @RestController
    @Validated
    static class TestController {

        @GetMapping("/test/custom-exception")
        public void throwCustomException() {
            throw new DiscodeitException(ErrorCode.ENDPOINT_NOT_FOUND);
        }

        @PostMapping("/test/validation")
        public void throwValidation(@Valid @RequestBody TestDto dto) {
        }

        @GetMapping("/test/param-validation")
        public void throwParamValidation(@RequestParam @Min(10) Integer value) {
        }

        @GetMapping("/test/type-mismatch")
        public void throwTypeMismatch(@RequestParam Integer value) {
        }

        @GetMapping("/test/missing-param")
        public void throwMissingParam(@RequestParam String required) {
        }

        @PostMapping("/test/missing-part")
        public void throwMissingPart(@RequestPart("file") MultipartFile file) {
        }

        @GetMapping("/test/missing-cookie")
        public void throwMissingCookie(@CookieValue("sessionId") String sessionId) {
        }

        @GetMapping("/test/internal-error")
        public void throwInternalError() {
            throw new RuntimeException("Unexpected error");
        }

        @DeleteMapping("/test/method-not-allowed")
        public void deleteOnly() {
        }
    }

    @Getter
    @NoArgsConstructor
    static class TestDto {
        @NotNull(message = "name은 필수입니다")
        private String name;
    }

    @Nested
    @DisplayName("DiscodeitException")
    class DiscodeitExceptionTest {

        @Test
        @DisplayName("비즈니스 예외 발생 시 정해진 포맷으로 응답")
        void handleDiscodeitException() throws Exception {
            mockMvc.perform(get("/test/custom-exception"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENDPOINT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details.path").value("/test/custom-exception"));
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException")
    class MethodArgumentNotValidExceptionTest {

        @Test
        @DisplayName("@Valid 위반 시 fieldErrors 반환")
        void handleMethodArgumentNotValid() throws Exception {
            TestDto invalidDto = new TestDto();

            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_BODY_VALUE"))
                .andExpect(jsonPath("$.details.fieldErrors").isArray())
                .andExpect(jsonPath("$.details.fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("$.details.fieldErrors[0].message").value("name은 필수입니다"))
                .andExpect(jsonPath("$.details.fieldErrors[0].rejectedValue").doesNotExist());
        }
    }

    @Nested
    @DisplayName("ConstraintViolationException")
    class ConstraintViolationExceptionTest {

        @Test
        @DisplayName("@Validated 위반 시 violations 반환")
        void handleConstraintViolation() throws Exception {
            mockMvc.perform(get("/test/param-validation").param("value", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER_VALUE"))
                .andExpect(jsonPath("$.details.violations").isArray())
                .andExpect(jsonPath("$.details.violations[0].property")
                    .value(Matchers.containsString("value")));
        }
    }

    @Nested
    @DisplayName("HttpMessageNotReadableException")
    class HttpMessageNotReadableExceptionTest {

        @Test
        @DisplayName("잘못된 JSON 형식 요청 시 INVALID_JSON 반환")
        void handleInvalidJson() throws Exception {
            String brokenJson = "{ \"name\": \"test\"";

            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(brokenJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_JSON"))
                .andExpect(jsonPath("$.details.cause").exists());
        }
    }

    @Nested
    @DisplayName("MissingServletRequestParameterException")
    class MissingServletRequestParameterExceptionTest {

        @Test
        @DisplayName("필수 파라미터 누락 시 MISSING_PARAMETER 반환")
        void handleMissingParameter() throws Exception {
            mockMvc.perform(get("/test/missing-param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_PARAMETER"))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("required")));
        }
    }

    @Nested
    @DisplayName("MissingServletRequestPartException")
    class MissingServletRequestPartExceptionTest {

        @Test
        @DisplayName("필수 파트 누락 시 MISSING_PART 반환")
        void handleMissingPart() throws Exception {
            mockMvc.perform(multipart("/test/missing-part")
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_PART"))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("file")));
        }
    }

    @Nested
    @DisplayName("MissingRequestCookieException")
    class MissingRequestCookieExceptionTest {

        @Test
        @DisplayName("필수 쿠키 누락 시 MISSING_COOKIE 반환")
        void handleMissingCookie() throws Exception {
            mockMvc.perform(get("/test/missing-cookie"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_COOKIE"))
                .andExpect(jsonPath("$.details.cookieName").value("sessionId"));
        }
    }

    @Nested
    @DisplayName("MethodArgumentTypeMismatchException")
    class MethodArgumentTypeMismatchExceptionTest {

        @Test
        @DisplayName("파라미터 타입 불일치 시 에러 메시지 반환")
        void handleTypeMismatch() throws Exception {
            mockMvc.perform(get("/test/type-mismatch").param("value", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER_VALUE"))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Integer")));
        }
    }

    @Nested
    @DisplayName("NoHandlerFoundException / NoResourceFoundException")
    class NotFoundExceptionTest {

        @Test
        @DisplayName("정의되지 않은 URL 요청 시 404 반환")
        void handleNoHandlerFound() throws Exception {
            mockMvc.perform(get("/api/unknown/url/path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENDPOINT_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("HttpRequestMethodNotSupportedException")
    class HttpRequestMethodNotSupportedExceptionTest {

        @Test
        @DisplayName("지원하지 않는 HTTP 메서드 요청 시 405 반환")
        void handleMethodNotAllowed() throws Exception {
            mockMvc.perform(get("/test/method-not-allowed"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
        }
    }

    @Nested
    @DisplayName("HttpMediaTypeNotSupportedException")
    class HttpMediaTypeNotSupportedExceptionTest {

        @Test
        @DisplayName("지원하지 않는 Content-Type 요청 시 415 반환")
        void handleUnsupportedMediaType() throws Exception {
            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("plain text"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"));
        }
    }

    @Nested
    @DisplayName("Exception (fallback)")
    class FallbackExceptionTest {

        @Test
        @DisplayName("예상치 못한 예외 발생 시 500 반환")
        void handleInternalError() throws Exception {
            mockMvc.perform(get("/test/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
        }
    }
}
