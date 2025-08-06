package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class BasicUserService implements UserService {

    private  final UserRepository userRepository;
    private Map<UUID, User> data;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.data = userRepository.loadData();
    }


    @Override
    public void create(User user) {
        data.put(user.getId(), user);
        userRepository.save(data);
    }

    @Override
    public User find(UUID id) {
        return data.get(id);
    }

    @Override
    public ArrayList<User> allFind() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, User user) {
        if (data.containsKey(id)) {
            data.put(id, user);
            userRepository.save(data);
        }
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) != null) {
            userRepository.save(data);
        }
    }
}
