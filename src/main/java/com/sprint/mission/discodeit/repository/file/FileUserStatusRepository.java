package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("dev")
public class FileUserStatusRepository extends FileBaseRepository<UserStatus> implements UserStatusRepository {
    public FileUserStatusRepository(AppStorageProperties storageProperties) {
        super(UserStatus.class, storageProperties);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) return Optional.empty();
        return findAll().stream()
                .filter(userStatus -> userId.equals(userStatus.getUserId()))
                .findFirst();
    }

    @Override
    public UserStatus getOrThrowByUserId(UUID userId) {
        return findByUserId(Objects.requireNonNull(userId, "userId must not be null"))
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + userId));
    }


    @Override
    public boolean existsByUserId(UUID userId) {
        if (userId == null) return false;
        return findByUserId(userId).isPresent();

    }

    @Override
    public boolean deleteByUserId(UUID userId) {
        if (userId == null) return false;
        return findByUserId(userId)
                .filter(us -> deleteById(us.getId()))
                .isPresent();
    }

    @Override
    public boolean hardDeleteByUserId(UUID userId) {
        if (userId == null) return false;
        return findByUserId(userId)
                .filter(us -> hardDeleteById(us.getId()))
                .isPresent();
    }
}
