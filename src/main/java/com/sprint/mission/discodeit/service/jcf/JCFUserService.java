package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private static final JCFUserService instance = new JCFUserService();
    Map<UUID, User> users = new HashMap<>();

    public static JCFUserService getInstance() {
        return instance;
    }

    @Override
    public void create(User user) {
        if (users.containsKey(user.getId())) {
            return;
        }
        users.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        User user = users.get(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
         List<User> list = new ArrayList<>(users.values());
         for (User user : list) {
             System.out.println(user);
         }
         return list;
    }

    @Override
    public void update(UUID id, String nickName) {
        User user = users.get(id);
        if (user != null) {
            user.updateNickName(nickName);
        }
    }

    @Override
    public void delete(String nickName) {
        users.remove(nickName);
    }
}
