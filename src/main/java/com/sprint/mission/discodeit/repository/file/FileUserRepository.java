package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

    public FileUserRepository() {
        super("users");
    }

    @Override
    public Optional<User> findByName(String username) {

        return data.values().stream()
            .filter(u -> u.getName().equals(username))
            .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return data.values().stream()
            .anyMatch(u -> u.getName().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return data.values().stream()
            .anyMatch(u -> u.getEmail().equals(email));
    }
}