package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.BaseEntity;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BaseRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class JcfBaseRepository<T extends BaseEntity> implements BaseRepository<T> {

    protected final Map<UUID, T> data = new HashMap<>();

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<T> saveAll(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream().map(this::save).toList();
    }

    @Override
    public Optional<T> findById(UUID id) {
        T e = data.get(id);
        return (e != null && !e.isDeleted()) ? Optional.of(e) : Optional.empty();
    }

    @Override
    public Optional<T> findByIdIncludingDeleted(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public T getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
                new NotFoundException("엔티티(" + getEntityTypeName() + ")를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<T> findAll() {
        return data.values().stream().filter(e -> !e.isDeleted()).toList();
    }

    @Override
    public List<T> findAllIncludingDeleted() {
        return List.copyOf(data.values());
    }

    @Override
    public List<T> findAllDeleted() {
        return data.values().stream().filter(BaseEntity::isDeleted).toList();
    }

    @Override
    public List<T> findAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .filter(e -> !e.isDeleted())
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public boolean existsAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return false;
        return ids.stream().allMatch(this::existsById);
    }

    @Override
    public boolean softDeleteById(UUID id) {
        T entity = data.get(id);
        if (entity != null && !entity.isDeleted()) {
            entity.delete();
            return true;
        }
        return false;
    }

    @Override
    public int softDeleteAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = 0;
        for (UUID id : ids) {
            if (softDeleteById(id)) count++;
        }
        return count;
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
    public int restoreAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = 0;
        for (UUID id : ids) {
            if (restoreById(id)) count++;
        }
        return count;
    }

    @Override
    public boolean hardDeleteById(UUID id) {
        return data.remove(id) != null;
    }

    @Override
    public int hardDeleteAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = 0;
        for (UUID id : ids) {
            if (hardDeleteById(id)) count++;
        }
        return count;
    }

    @Override
    public int hardDeleteAllExpired(Instant now) {
        Instant ref = (now != null) ? now : Instant.now();
        List<UUID> toRemove = data.values().stream()
                .filter(BaseEntity::isDeleted)
                .filter(e -> e.shouldPurge(ref))
                .map(BaseEntity::getId)
                .toList();
        toRemove.forEach(data::remove);
        return toRemove.size();
    }

    @Override
    public long count() {
        return data.values().stream().filter(e -> !e.isDeleted()).count();
    }

    @Override
    public long countIncludingDeleted() {
        return data.size();
    }

    protected String getEntityTypeName() {
        return "Entity";
    }
}
