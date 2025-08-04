package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.util.*;

public class AbstractJCFRepository<T extends BaseEntity> {
    protected final Map<UUID, T> data;

    protected AbstractJCFRepository() {
        this.data = new HashMap<>();
    }

    public T save(T entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    public T findById(UUID id) {
        return data.get(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    public void deleteById(UUID id) {
        data.remove(id);
    }

    public void deleteAll() {
        data.clear();
    }
}