package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    final Map<UUID, User> data;
    public JCFUserRepository() {
        data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public boolean delete(UUID id) {
        return  data.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public boolean update(UUID id, String username, String password) {
        User user = data.get(id);
        if(user.getUsername().equals(username) && user.getPassword().equals(password)){
            System.out.println("수정 전과 일치합니다.");
            return false;
        }

        user.update(username, password);
        return true;
    }
}
