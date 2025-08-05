package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {
    private final List<User> userList = new ArrayList<>();

    @Override
    public void save(User user) {
        userList.add(user);
    }

    @Override
    public User findById(UUID id) {
        return userList.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userList.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByUserName(String userName) {
        return userList.stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findByNickName(String nickname) {
        return userList.stream()
                .filter(user -> user.getNickname().equals(nickname))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return userList;
    }

    @Override
    public boolean updateByEmail(String email, String userName, String nickname, String password, String phoneNumber) {
        User user = findByEmail(email);
        if (user == null) return false;

        user.updateUser(email, userName, nickname, password, phoneNumber);
        return true;
    }

    @Override
    public boolean updateByUserName(String userName, String email, String nickname, String password, String phoneNumber) {
        User user = findByUserName(userName);
        if (user == null) return false;

        user.updateUser(email, userName, nickname, password, phoneNumber);
        return true;
    }

    @Override
    public boolean deleteByEmail(String email) {
        return userList.remove(findByEmail(email));
    }

    @Override
    public boolean deleteByUserName(String userName) {
        return userList.remove(findByUserName(userName));
    }
}
