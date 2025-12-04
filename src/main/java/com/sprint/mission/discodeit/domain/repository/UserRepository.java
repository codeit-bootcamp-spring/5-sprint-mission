package com.sprint.mission.discodeit.domain.repository;

import com.sprint.mission.discodeit.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @NonNull
    @EntityGraph(attributePaths = {"profile"})
    List<User> findAll();

    @EntityGraph(attributePaths = {"profile"})
    List<User> findAllByIdIn(Collection<UUID> ids);

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
