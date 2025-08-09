package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("userRepository")
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        this.data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.data.values().stream().anyMatch(user -> user.getUsername().equals(username));
    }
    // anyMatch() : 조건에 맞는 게 하나라도 있으면 true 반환

    @Override
    public boolean existsByEmail(String email) {
        return this.data.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }


    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
