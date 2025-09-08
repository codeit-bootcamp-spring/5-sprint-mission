package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path USER_STATUS_DIR = Path.of(UserStatus.class.getSimpleName());

    public FileUserStatusRepository() {
        FileUtils.init(USER_STATUS_DIR);
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = USER_STATUS_DIR.resolve(userStatus.getId().toString());
        FileUtils.save(path, userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path path = USER_STATUS_DIR.resolve(id.toString());
        return Optional.ofNullable(FileUtils.findOne(path, UserStatus.class));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return this.findAll().stream()
                .filter(userStatus -> userStatus.getUser().getId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return FileUtils.findAll(USER_STATUS_DIR, UserStatus.class);
    }

    @Override
    public void delete(UUID id) {
        Path path = USER_STATUS_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        findByUserId(userId)
                .ifPresent(userStatus -> this.delete(userStatus.getId()));
    }
}
