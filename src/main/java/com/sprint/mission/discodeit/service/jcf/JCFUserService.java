package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public User create(User user) {
        return data.put(user.getId(), user);
    }

    @Override
    public User updateName(UUID id, String name) {
        User user = searchById(id);
        user.updateName(name);
        return user;
    }

    @Override
    public User addChannel(UUID id, Channel channel) {
        User user = searchById(id);
        user.addChannel(channel);
        return user;
    }

    @Override
    public User deleteChannel(UUID id, Channel channel) {
        User user = searchById(id);
        user.deleteChannel(channel);
        return user;
    }


    @Override
    public User delete(UUID id) {
        User user = searchById(id);
        return data.remove(user.getId());
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
        if(users.isEmpty()) {
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
