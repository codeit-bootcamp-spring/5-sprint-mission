package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserRepository implements UserRepository {
    private final Path USER_DIR = Path.of(User.class.getSimpleName());

    public FileUserRepository() {
        FileUtils.init(USER_DIR);
    }

    @Override
    public User save(User user) {
        Path path = USER_DIR.resolve(user.getId().toString());
        FileUtils.save(path, user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path path = USER_DIR.resolve(id.toString());
        return Optional.ofNullable(FileUtils.findOne(path, User.class));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return FileUtils.findAll(USER_DIR, User.class);
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = USER_DIR.resolve(id.toString());
        return FileUtils.fileExists(path);
    }

    @Override
    public void delete(UUID id) {
        Path path = USER_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    public void deleteAll() {
        FileUtils.deleteAll(USER_DIR);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
