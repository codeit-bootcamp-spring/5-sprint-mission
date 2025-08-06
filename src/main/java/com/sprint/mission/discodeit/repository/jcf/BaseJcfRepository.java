package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BaseJcfRepository<T extends BaseEntity> implements BaseRepository<T> {
    protected final Map<UUID, T> data = new ConcurrentHashMap<>();

    @Override
    public T save(T entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(data.get(id)).filter(c -> !c.isDeleted());
    }

    @Override
    public List<T> findAll() {
        return data.values().stream().filter(c -> !c.isDeleted()).toList();
    }

    @Override
    public List<T> findAllIncludingDeleted() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<T> findAllByIds(Set<UUID> ids) {
        List<T> result = new ArrayList<>();
        for (UUID id : ids) {
            Optional.ofNullable(data.get(id))
                    .filter(c -> !c.isDeleted())
                    .ifPresent(result::add);
        }
        return result;
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id) && !data.get(id).isDeleted();
    }

    @Override
    public boolean deleteById(UUID id) {
        T entity = data.get(id);
        if (entity != null && !entity.isDeleted()) {
            entity.delete();
            return true;
        }
        return false;
    }

    @Override
    public boolean hardDeleteById(UUID id) {
        return data.remove(id) != null;
    }

    @Override
    public boolean restoreById(UUID id) {
        T entity = data.get(id);
        if (entity != null && entity.isDeleted()) {
            entity.restore();
            return true;
        }
        return false;
    }

    @Override
    public long count() {
        return data.values().stream().filter(c -> !c.isDeleted()).count();
    }
}
