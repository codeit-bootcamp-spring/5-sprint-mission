package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpaCsrfTokenRequestHandler 단위 테스트")
class SpaCsrfTokenRequestHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private CsrfToken csrfToken;

    private SpaCsrfTokenRequestHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SpaCsrfTokenRequestHandler();
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("handle - csrfToken.get()을 호출하여 토큰을 로드한다")
    void handle_LoadsCsrfToken() {
        // given
        Supplier<CsrfToken> tokenSupplier = mock(Supplier.class);
        given(tokenSupplier.get()).willReturn(csrfToken);

        // when
        handler.handle(request, response, tokenSupplier);

        // then
        verify(tokenSupplier).get();
    }

    @Test
    @DisplayName("resolveCsrfTokenValue - 헤더에 값이 있으면 plain 핸들러로 처리한다")
    void resolveCsrfTokenValue_WithHeader_UsesPlainHandler() {
        // given
        String headerName = "X-CSRF-TOKEN";
        String headerValue = "csrf-token-value";

        given(csrfToken.getHeaderName()).willReturn(headerName);
        given(request.getHeader(headerName)).willReturn(headerValue);

        // when
        String result = handler.resolveCsrfTokenValue(request, csrfToken);

        // then
        assertThat(result).isEqualTo(headerValue);
    }

    @Test
    @DisplayName("resolveCsrfTokenValue - 헤더에 값이 없으면 XOR 핸들러로 처리한다")
    void resolveCsrfTokenValue_WithoutHeader_UsesXorHandler() {
        // given
        String headerName = "X-CSRF-TOKEN";
        String parameterName = "_csrf";
        given(csrfToken.getHeaderName()).willReturn(headerName);
        given(csrfToken.getParameterName()).willReturn(parameterName);
        given(request.getHeader(headerName)).willReturn(null);
        given(request.getParameter(parameterName)).willReturn(null);

        // when
        String result = handler.resolveCsrfTokenValue(request, csrfToken);

        // then
        assertThat(result).isNull();
    }

}
