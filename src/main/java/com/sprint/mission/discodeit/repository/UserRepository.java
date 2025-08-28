package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

    default User getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "User with id %s not found".formatted(id))
        );
    }

    @Query("""
        SELECT new com.sprint.mission.discodeit.dto.user.UserDto(
            u.id,
            u.username,
            u.email,
            p,
            (s.lastActiveAt >= :onlineSince)
        )
        FROM User u
        JOIN UserStatus s on s.user = u
        LEFT JOIN u.profile p
        """
    )
    List<UserDto> findAllDto(@Param("onlineSince") Instant onlineSince);
}
