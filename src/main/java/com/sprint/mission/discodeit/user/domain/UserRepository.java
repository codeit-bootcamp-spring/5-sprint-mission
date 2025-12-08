package com.sprint.mission.discodeit.user.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"profile"})
    @Query("SELECT u FROM User u")
    List<User> findAllWithProfile();

    @EntityGraph(attributePaths = {"profile"})
    List<User> findAllWithProfileByIdIn(Collection<UUID> ids);

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findWithProfileById(UUID userId);

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findWithProfileByUsername(String username);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
