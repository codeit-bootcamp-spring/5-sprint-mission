package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("prod")
public class JpaUserRepository extends JpaBaseRepository<User> implements UserRepository {

    public JpaUserRepository() {
        super(User.class);
    }

    private List<User> searchByPrefix(String field, String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();

        String jpql = String.format("SELECT u FROM User u WHERE LOWER(u.%s) LIKE CONCAT(LOWER(:value), '%%') AND u.deleted = false", field);
        return entityManager.createQuery(jpql, User.class)
                .setParameter("value", value.trim())
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email AND u.deleted = false", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username AND u.deleted = false", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.deleted = false", Long.class)
                .setParameter("email", email)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username AND u.deleted = false", Long.class)
                .setParameter("username", username)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchByEmail(String email) {
        return searchByPrefix("email", email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchByUsername(String username) {
        return searchByPrefix("username", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchByGlobalName(String globalName) {
        return searchByPrefix("globalName", globalName);
    }
}
