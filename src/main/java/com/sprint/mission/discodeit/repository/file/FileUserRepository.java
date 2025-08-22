package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserRepository implements UserRepository {
    private final Path USER_DIR = Path.of("user");

    public FileUserRepository() {
        FileUtils.init(USER_DIR);
    }

    @Override
    public User save(User userDto) {
        Path path = USER_DIR.resolve(userDto.getId().toString());
        FileUtils.save(path, userDto);
        return userDto;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path path = USER_DIR.resolve(id.toString());
        return Optional.ofNullable(FileUtils.findOne(path, User.class));
    }

    @Override
    public List<User> findAll() {
        return FileUtils.findAll(USER_DIR, User.class);
    }

    @Override
    public void delete(UUID id) {
        Path path = USER_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    @Override
    public void deleteAll() {
        FileUtils.deleteAll(USER_DIR);
    }
}
