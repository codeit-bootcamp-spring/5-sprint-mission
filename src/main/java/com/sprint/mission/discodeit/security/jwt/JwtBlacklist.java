package com.sprint.mission.discodeit.security.jwt;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JwtBlacklist {

    // 블랙리스트를 저장하는 ConcurrentHashMap
    // accessToken, 만료시간 형태로 저장
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    // accessToken을 블랙리스트에 등록
    public void put(String accessToken, Instant expirationTime) {
        blacklist.putIfAbsent(accessToken, expirationTime); // key가 이미 있을 시 기존 값 유지
    }

    // accessToken의 블랙리스트 등록 여부 확인
    public boolean contains(String accessToken) {
        return blacklist.containsKey(accessToken);
    }

    // 만료되어 블랙리스트 등록이 필요없는 토큰을 1시간 주기로 정리
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void clean() {
        // 만료 시간이 현재보다 과거인지 확인 후 제거
        blacklist.values().removeIf(expirationTime -> expirationTime.isBefore(Instant.now()));
    }
}
