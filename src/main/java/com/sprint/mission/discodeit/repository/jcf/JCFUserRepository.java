package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        data = new HashMap<>();
    }

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public void delete(User user) {
        if(!data.containsKey(user.getId())) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        data.remove(user.getId());
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public User searchById(UUID id) {
        if(!data.containsKey(id)) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return data.get(id);
    }

    @Override
    public List<User> searchByName(String name) {
        List<User> users = new ArrayList<>();
        for (User user : data.values()) {
            if (user.getName().contains(name)) {
                users.add(user);
            }
        }
        if (users.isEmpty()) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return users;
    }

    @Override
    public List<User> searchAll() {
        return new ArrayList<>(data.values());
    }
}
