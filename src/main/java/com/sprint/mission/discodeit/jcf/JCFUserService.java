package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void create(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        if  (users.containsKey(id)) {
            return users.get(id);
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void update(UUID id, String name, int age) {
        User user = users.get(id);
        if (user != null) {
            user.update(name, age);
        }
    }

    @Override
    public boolean joinChannel(UUID id, UUID channelId) {
        User user = users.get(id);
        if (user == null) return false;
        return user.joinChannel(channelId);
    }

    @Override
    public boolean leaveChannel(UUID id, UUID channelId) {
        User user = users.get(id);
        if (user == null) return false;
        return user.leaveChannel(channelId);
    }

    @Override
    public boolean addMessage(UUID id, UUID messageId) {
        User user = users.get(id);
        if (user == null) return false;
        return user.addMessage(messageId);
    }

    @Override
    public boolean removeMessage(UUID id, UUID messageId) {
        User user = users.get(id);
        if (user == null) return false;
        return user.removeMessage(messageId);
    }

    @Override
    public void delete(UUID id) {
        users.remove(id);
    }

    @Override
    public boolean isUserInChannel(UUID userId, UUID channelId) {
        User user = users.get(userId);
        if (user == null) return false;
        return user.getChannelIds().contains(channelId);
    }

    @Override
    public String toString() {
        return "JCFUserService{" +
                "users=" + users +
                '}';
    }
}
