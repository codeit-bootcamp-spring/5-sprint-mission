package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

<<<<<<< HEAD
    public  JCFUserRepository() {
=======
    public JCFUserRepository() {
>>>>>>> 717adae (feat: 초기 커밋)
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        this.data.put(user.getId(), user);
        return user;
    }

    @Override
<<<<<<< HEAD
    public Optional<User> find(UUID userId) {
        return Optional.ofNullable(this.data.get(userId));
=======
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
>>>>>>> 717adae (feat: 초기 커밋)
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
<<<<<<< HEAD
    public boolean existById(UUID userId) {
        return data.containsKey(userId);
    }

    @Override
    public void delete(UUID userId) {
        if (!this.data.containsKey(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        this.data.remove(userId);
=======
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
>>>>>>> 717adae (feat: 초기 커밋)
    }
}
