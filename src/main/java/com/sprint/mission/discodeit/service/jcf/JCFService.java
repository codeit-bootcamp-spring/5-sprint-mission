package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.service.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class JCFService<T> implements Service<T> {
    protected final List<T> data = new ArrayList<>();

    @Override
    public void reset() {
        data.clear();
    }

    @Override
    public List<T> findAll() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public T findById(UUID id) {
        return data.stream()
                .filter(e -> idEquals(e, id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(UUID id) {
        data.stream()
                .filter(e -> idEquals(e, id))
                .findFirst()
                .ifPresent(data::remove);
    }

    protected abstract boolean idEquals(T entity, UUID id);
}
