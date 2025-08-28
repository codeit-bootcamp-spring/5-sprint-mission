package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

    default UserStatus getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "UserStatus with id %s not found".formatted(id))
        );
    }

    Optional<UserStatus> findByUserId(UUID userId);

    // Map<UUID, UserStatusType> findAllTypesByUserIds(Set<UUID> userIds);

    boolean deleteByUserId(UUID userId);
}
