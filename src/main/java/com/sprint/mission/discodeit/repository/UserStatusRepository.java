package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    Optional<UserStatus> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("""
            SELECT us FROM UserStatus us
            WHERE us.user.id IN :userIds
            """)
    List<UserStatus> findByUserIdIn(Collection<UUID> userIds);
}
