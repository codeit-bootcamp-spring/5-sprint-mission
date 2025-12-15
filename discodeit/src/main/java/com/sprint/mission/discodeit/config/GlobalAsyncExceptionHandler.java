package com.sprint.mission.discodeit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class GlobalAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("비동기 처리 중 예외 발생 - method={}, message={}",
                method.getName(), ex.getMessage(), ex);

        // 필요하면 여기서 추가 작업 가능
        // 예: 슬랙 알림, 메트릭 전송, 관리자 이메일 보내기 등
        log.error("비동기 메서드 파라미터 값들:");
        for (Object param : params) {
            log.error("  > {}", param);
        }
    }
}
