package com.sprint.mission.discodeit.repositoryprod.impl;

import com.sprint.mission.discodeit.domain.entityprod.ProdBaseEntity;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repositoryprod.ProdBaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class JpaBaseRepository<T extends ProdBaseEntity> implements ProdBaseRepository<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected final Class<T> entityType;

    protected JpaBaseRepository(Class<T> entityType) {
        this.entityType = entityType;
    }

    @Override
    @Transactional
    public T save(T entity) {
        if (entity == null) throw new IllegalArgumentException("entity must not be null");

        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        }

        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public List<T> saveAll(Collection<T> entities) {
        return entities.stream()
                .map(this::save)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(UUID id) {
        T found = entityManager.find(entityType, id);
        return Optional.ofNullable(found).filter(e -> !e.isDeleted());
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return entityManager.createQuery(
                        "SELECT e FROM " + entityType.getSimpleName() + " e WHERE e.deleted = false",
                        entityType)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllIncludingDeleted() {
        return entityManager.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        return entityManager.createQuery(
                        "SELECT e FROM " + entityType.getSimpleName() + " e WHERE e.id IN :ids AND e.deleted = false",
                        entityType)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public T getOrThrow(UUID id) {
        return findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("엔티티(%s)를 찾을 수 없습니다: %s", entityType.getSimpleName(), id)));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        if (id == null) return false;

        Long count = entityManager.createQuery(
                        "SELECT COUNT(e) FROM " + entityType.getSimpleName() + " e WHERE e.id = :id AND e.deleted = false",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return false;

        Long count = entityManager.createQuery(
                        "SELECT COUNT(e) FROM " + entityType.getSimpleName() + " e WHERE e.id IN :ids AND e.deleted = false",
                        Long.class)
                .setParameter("ids", ids)
                .getSingleResult();

        return count == ids.size();
    }

    @Override
    @Transactional
    public boolean softDeleteById(UUID id) {
        T entity = entityManager.find(entityType, id);
        if (entity == null || entity.isDeleted()) return false;

        entity.delete();
        entityManager.merge(entity);
        return true;
    }

    @Override
    @Transactional
    public int softDeleteAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        List<T> entities = entityManager.createQuery(
                        "SELECT e FROM " + entityType.getSimpleName() + " e WHERE e.id IN :ids AND e.deleted = false",
                        entityType)
                .setParameter("ids", ids).getResultList();

        for (T entity : entities) {
            entity.delete();
            entityManager.merge(entity);
        }

        return entities.size();
    }

    @Override
    @Transactional
    public boolean restoreById(UUID id) {
        T entity = entityManager.find(entityType, id);
        if (entity == null || !entity.isDeleted()) return false;

        entity.restore();
        entityManager.merge(entity);
        return true;
    }

    @Override
    @Transactional
    public int restoreAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        List<T> entities = entityManager.createQuery(
                "SELECT e FROM " + entityType.getSimpleName() + " e WHERE e.id IN :ids AND e.deleted = true", entityType
        ).setParameter("ids", ids).getResultList();

        for (T entity : entities) {
            entity.restore();
            entityManager.merge(entity);
        }

        return entities.size();
    }

    @Override
    @Transactional
    public boolean hardDeleteById(UUID id) {
        T entity = entityManager.find(entityType, id);
        if (entity == null) return false;

        entityManager.remove(entity);
        return true;
    }

    @Override
    @Transactional
    public int hardDeleteAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        List<T> entities = entityManager.createQuery(
                "SELECT e FROM " + entityType.getSimpleName() + " e WHERE e.id IN :ids", entityType
        ).setParameter("ids", ids).getResultList();

        for (T entity : entities) entityManager.remove(entity);

        return entities.size();
    }


    @Override
    @Transactional(readOnly = true)
    public long count() {
        return entityManager.createQuery(
                "SELECT COUNT(e) FROM " + entityType.getSimpleName() + " e WHERE e.deleted = false", Long.class
        ).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public long count(Predicate<T> condition) {
        List<T> all = entityManager.createQuery(
                "SELECT e FROM " + entityType.getSimpleName() + " e WHERE e.deleted = false", entityType
        ).getResultList();

        return all.stream()
                .filter(condition)
                .count();
    }
}
