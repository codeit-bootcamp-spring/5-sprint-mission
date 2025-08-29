package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

    Optional<UserStatus> findByUserId(UUID userId);

    int deleteAllByUserId(UUID userId);

    default UserStatus getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "UserStatus with id %s not found".formatted(id))
        );
    }
}
