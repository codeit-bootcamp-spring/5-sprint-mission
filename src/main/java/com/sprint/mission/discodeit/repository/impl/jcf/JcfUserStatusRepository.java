package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@Profile("test")
public class JcfUserStatusRepository extends AbstractJcfRepository<UserStatus> implements UserStatusRepository {

    @Override
    protected String getEntityTypeName() {
        return "UserStatus";
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        return findAll().stream().filter(us -> userId.equals(us.getUserId())).findFirst();
    }

    @Override
    public List<UserStatus> findAllByUserIds(Collection<UUID> userIds) {
        Objects.requireNonNull(userIds, "userIds must not be null");
        if (userIds.isEmpty()) return List.of();
        Set<UUID> set = userIds instanceof java.util.Set<UUID> s ? s : new HashSet<>(userIds);
        return findAll().stream().filter(us -> set.contains(us.getUserId())).toList();
    }

    @Override
    public UserStatus getOrThrowByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        return findByUserId(userId).orElseThrow(() -> new NotFoundException("UserStatus not found: " + userId));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        return findByUserId(userId).isPresent();
    }

    @Override
    public boolean softDeleteByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        var found = findByUserId(userId);
        return found.map(v -> softDeleteById(v.getId())).orElse(false);
    }

    @Override
    public boolean hardDeleteByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        var found = findByUserId(userId);
        return found.map(v -> hardDeleteById(v.getId())).orElse(false);
    }
}
