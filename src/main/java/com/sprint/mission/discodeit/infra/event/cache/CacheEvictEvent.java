package com.sprint.mission.discodeit.infra.event.cache;

public record CacheEvictEvent(
    String cacheName,
    String key
) {
}
