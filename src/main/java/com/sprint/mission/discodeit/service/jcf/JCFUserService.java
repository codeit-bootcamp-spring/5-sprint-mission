package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

//    final List<User> data = new ArrayList<>();
    final Map<UUID, User> data = new HashMap<>();


    @Override
    public User createUser(String username, String password) {
        if(username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("username or password ois null or blank");
        }
        User user = new User(username, password);
        data.put(user.getId(), user);
        return user;
    }


    @Override
    public User find(UUID userId){
        if(!data.containsKey(userId)){
            throw new NoSuchElementException("user not found");
        }
        return data.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID userId, String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is null or blank");
        }

        User existingUser = data.get(userId);
        if (existingUser == null) {
            throw new NoSuchElementException("User not found");
        }

        // 새 User 인스턴스를 생성 (기존 데이터 재활용)
        User updatedUser = new User(username, password);
        data.put(userId, updatedUser);
        return updatedUser;
    }

    @Override
    public User delete(UUID userId) {
        User user = data.remove(userId);
        if(user == null){
            throw new NoSuchElementException("user not found");
        }
        return user;
    }
}
