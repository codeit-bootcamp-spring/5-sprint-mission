package com.sprint.mission.discodeit.config;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class CodeitLevelCache implements Cache {

    private final String name;
    private final Cache l1; // Caffeine
    private final Cache l2; // Redis

    public CodeitLevelCache(String name, Cache l1, Cache l2) {
        this.name = name;
        this.l1 = l1;
        this.l2 = l2;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        // 1) L1 조회
        ValueWrapper v1 = l1.get(key);
        if (v1 != null) {
            return v1;
        }

        // 2) L2 조회
        ValueWrapper v2 = l2.get(key);

        // L2에서 가져온 값이 있으면 L1에 넣어서 Multi-Level Cache 효과
        if (v2 != null) {
            l1.put(key, v2.get());
        }
        return v2;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper wrapper = get(key);
        return wrapper == null ? null : (T) wrapper.get();
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        // L1 조회
        ValueWrapper v1 = l1.get(key);
        if (v1 != null) {
            return (T) v1.get();
        }

        // L2 조회
        ValueWrapper v2 = l2.get(key);
        if (v2 != null) {
            T value = (T) v2.get();
            // L1 보충
            l1.put(key, value);
            return value;
        }

        // L1/L2 모두 캐시 미스 → DB 조회 또는 실제 수행
        try {
            T value = valueLoader.call();

            // 두 레벨 캐시 모두 저장
            l1.put(key, value);
            l2.put(key, value);

            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        l1.put(key, value);
        l2.put(key, value);
    }

    @Override
    public void evict(Object key) {
        l1.evict(key);
        l2.evict(key);
    }

    @Override
    public void clear() {
        l1.clear();
        l2.clear();
    }
}
