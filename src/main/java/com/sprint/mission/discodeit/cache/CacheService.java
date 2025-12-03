package com.sprint.mission.discodeit.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    public void evictUserDetailsByUsername(String username) {
        if (!hasText(username)) {
            return;
        }

        Cache userDetailsCache = cacheManager.getCache("userDetails");
        if (userDetailsCache != null) {
            userDetailsCache.evict(username);
        }
    }
}
