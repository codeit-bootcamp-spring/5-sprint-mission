package com.sprint.mission.discodeit.global.cache;

import java.util.Collection;

public interface CacheService {

    void evict(String cacheName, Object key);

    void evictAll(String cacheName, Collection<?> keys);

    void clear(String cacheName);
}
