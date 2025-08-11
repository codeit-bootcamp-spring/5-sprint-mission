package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Repository
@Profile("test")
public class JcfUserStatusRepository extends JcfBaseRepository<UserStatus> implements UserStatusRepository {

    @Override
    protected String getEntityTypeName() {
        return "UserStatus";
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) return Optional.empty();
        return findAll().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAllByUserIds(Set<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        Set<UUID> ids = userIds.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
        if (ids.isEmpty()) return List.of();
        return findAll().stream()
                .filter(us -> ids.contains(us.getUserId()))
                .toList();
    }

    @Override
    public UserStatus getOrThrowByUserId(UUID userId) {
        return findByUserId(Objects.requireNonNull(userId, "userId must not be null"))
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + userId));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return findByUserId(userId).isPresent();
    }

    @Override
    public boolean deleteByUserId(UUID userId) {
        if (userId == null) return false;
        return findByUserId(userId).filter(userStatus -> deleteById(userStatus.getId())).isPresent();
    }

    @Override
    public boolean hardDeleteByUserId(UUID userId) {
        if (userId == null) return false;
        return findByUserId(userId).filter(userStatus -> hardDeleteById(userStatus.getId())).isPresent();
    }
}
