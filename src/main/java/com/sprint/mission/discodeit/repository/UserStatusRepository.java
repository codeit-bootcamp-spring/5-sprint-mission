package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

    Optional<UserStatus> findByUser(User user);

    Optional<UserStatus> findByUserId(UUID userId);

    default UserStatus getOrCreateByUser(User user) {
        return findByUser(user).orElseGet(() -> save(new UserStatus(user)));
    }

    default UserStatus getOrThrowByUserId(UUID userId) {
        return findByUserId(userId).orElseThrow(() ->
            new NotFoundException(
                "UserStatus for user with id %s not found".formatted(userId))
        );
    }

    default UserStatus getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "UserStatus with id %s not found".formatted(id))
        );
    }
}
