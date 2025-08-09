package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.deventity.DevBaseEntity;
import com.sprint.mission.discodeit.repository.BaseRepository;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Profile("test")
public class JcfBaseRepository<T extends DevBaseEntity> implements BaseRepository<T> {

    protected final Map<UUID, T> data = new HashMap<>();

    @Override
    public T save(T entity) {
        if (entity == null) throw new IllegalArgumentException("Entity must not be null");
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<T> saveAll(Collection<T> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::save).toList();
    }

    @Override
    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(data.get(id)).filter(c -> !c.isDeleted());
    }

    @Override
    public T getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
                new NoSuchElementException("엔티티(" + getEntityTypeName() + ")를 찾을 수 없습니다: " + id));
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
    public boolean existsAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return false;
        return ids.stream().allMatch(this::existsById);
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
    public int deleteAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        int count = 0;
        for (UUID id : ids) {
            if (deleteById(id)) count++;
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
    public int restoreAllByIds(Set<UUID> ids) {
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
    public int hardDeleteAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        int count = 0;
        for (UUID id : ids) {
            if (hardDeleteById(id)) count++;
        }
        return count;
    }

    @Override
    public long count() {
        return data.values().stream().filter(e -> !e.isDeleted()).count();
    }

    @Override
    public long count(Predicate<T> condition) {
        return data.values().stream()
                .filter(e -> !e.isDeleted())
                .filter(condition)
                .count();
    }

    protected String getEntityTypeName() {
        return "Entity";
    }
}
