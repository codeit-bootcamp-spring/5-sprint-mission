package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

//스프링 MVC에서 컨트롤러가 실행되기 전후에 중간작업을 할수있게 해준다
// MDC = Mapped Diagnostic Context
// 진단 정보를 담는 맵
// 로그를 찍을 때, 이 요청 ID, HTTP 메서드, URI 등을 자동으로 로그에 포함시킬 수 있어요.
public class MDCLoggingInterceptor implements HandlerInterceptor {
    private static final String REQUEST_ID_KEY = "requestId";   //MDC에서 사용할 키이름
    private static final String HEADER_NAME = "Discodeit-Request-ID"; //클라이언트에게 응답할때 헤더에 넣어줄 이름

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 요청 ID 생성
        // 나중에 로그에서 어떤 요청이 어떤 로그와 연결되는지 추적할 때 사용돼요.
        String requestId = UUID.randomUUID().toString();

        // 2. MDC에 저장
        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        // 3. 응답 헤더에도 추가
        response.setHeader(HEADER_NAME, requestId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // MDC 정리
        /*요청 처리 끝나면 MDC를 비워서 다음 요청에 영향을 주지 않도록 함.
        중요한 이유: Tomcat 같은 서블릿 컨테이너는 쓰레드를 재사용하기 때문에,
        MDC를 안 비우면 이전 요청의 데이터가 다음 로그에 섞일 수 있어요.*/
        MDC.clear();
    }


}
