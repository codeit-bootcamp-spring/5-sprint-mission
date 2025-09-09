package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = { "profile", "userStatus" })
    @Query("SELECT u FROM User u")
    List<User> findAllGraph();

    @EntityGraph(attributePaths = { "profile", "userStatus" })
    List<User> findAllByIdIn(Collection<UUID> ids);

    @EntityGraph(attributePaths = { "profile", "userStatus" })
    Optional<User> findByUsername(String username);

    default User getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "User with id %s not found".formatted(id))
        );
    }
}
