package com.sprint.mission.discodeit.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CodeitLevelCacheManager implements CacheManager {

    private final CacheManager l1Manager;          // Caffeine
    private final CacheManager l2Manager;          // Redis
    private final Set<String> codeitCacheNames;    // 멀티레벨 대상 캐시 이름들

    public CodeitLevelCacheManager(CacheManager l1, CacheManager l2, Set<String> names) {
        this.l1Manager = l1;
        this.l2Manager = l2;
        this.codeitCacheNames = names;
    }

    @Override
    public Cache getCache(String name) {
        if (codeitCacheNames.contains(name)) {
            Cache l1 = l1Manager.getCache(name);
            Cache l2 = l2Manager.getCache(name);
            if (l1 == null || l2 == null) {
                return null;
            }
            return new CodeitLevelCache(name, l1, l2);
        }

        Cache redisCache = l2Manager.getCache(name);
        if (redisCache != null) {
            return redisCache;
        }

        return l1Manager.getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        Set<String> names = new HashSet<>(codeitCacheNames);
        names.addAll(l1Manager.getCacheNames());
        names.addAll(l2Manager.getCacheNames());
        return names;
    }
}
