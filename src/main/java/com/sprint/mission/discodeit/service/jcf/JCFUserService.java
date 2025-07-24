package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private static final JCFUserService instance = new JCFUserService();
    private final Map<UUID, User> users = new HashMap<>();

    public static JCFUserService getInstance() {
        return instance;
    }

    @Override
    public void create(User user) {
        if (users.containsKey(user.getId())) {
            //return;
            throw new IllegalArgumentException("이미 존재하는 사용자입니다." + user.getId());
        }
        users.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        User user = users.get(id);
        if (user != null) {
            System.out.println("찾으신 닉네임: "+user.getName());
        } else {
            System.out.println("사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    @Override
    public User findByName(String nickName) {
        for (User user : users.values()) {
            if (user.getName().equals(nickName)) {
                System.out.println("찾으신 닉네임: " + user.getName());
                return user;
            }
        }
        return null;
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
            user.updatedName(nickName);
        }
    }

    @Override
    public void delete(UUID id) {
        users.remove(id);
    }
}
