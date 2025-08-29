package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
        SELECT new com.sprint.mission.discodeit.dto.user.UserDto(
            u.id,
            u.username,
            u.email,
            p.id,
            p.fileName,
            p.size,
            p.contentType,
            CASE WHEN us.lastActiveAt IS NOT NULL AND us.lastActiveAt >= :onlineSince
                 THEN TRUE ELSE FALSE END
        )
        FROM User u
        LEFT JOIN UserStatus us ON us.user = u
        LEFT JOIN u.profile p
        """
    )
    List<UserDto> findAllDto(@Param("onlineSince") Instant onlineSince);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findForUpdateById(@Param("id") UUID id);

    default User getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "User with id %s not found".formatted(id))
        );
    }

    default User getForUpdateOrThrow(UUID id) {
        return findForUpdateById(id).orElseThrow(() ->
            new NotFoundException(
                "User with id %s not found".formatted(id))
        );
    }
}
