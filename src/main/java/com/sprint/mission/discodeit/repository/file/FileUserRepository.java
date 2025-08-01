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

    // TODO mission 조건에 맞도록 추후 구현 existsByUsername, existsByEmail
    @Override
    public Optional<User> findByName(String username) {
        return Optional.empty();
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
}