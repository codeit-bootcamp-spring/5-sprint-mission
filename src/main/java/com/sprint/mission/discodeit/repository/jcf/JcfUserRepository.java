package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.Optional;

public class JcfUserRepository extends BaseJcfRepository<User> implements UserRepository {
    @Override
    public Optional<User> findByEmail(String email) {
        return data.values().stream()
                .filter(u -> !u.isDeleted() && u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return data.values().stream()
                .filter(u -> !u.isDeleted() && u.getUsername().equals(username))
                .findFirst();
    }
}
