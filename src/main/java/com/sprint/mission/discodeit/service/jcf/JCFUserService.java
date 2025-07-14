package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final List<User> userList;

    public JCFUserService() {
        this.userList = new ArrayList<>();
    }

    @Override
    public boolean createUser(String email, String userName, String nickname, String password, String phoneNumber) {
        for (User user : userList) {
            if (user.getEmail().equals(email)) return false;
            if (user.getUserName().equals(userName)) return false;
        }

        userList.add(new User(UUID.randomUUID(), Instant.now().getEpochSecond(), email, userName, nickname, password, phoneNumber));
        return true;
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
    public List<User> findAllUsers() {
        return userList;
    }

    @Override
    public boolean updateByEmail(String email, String userName, String nickname, String password, String phoneNumber) {
        for (User user : userList) {
            if (user.getEmail().equals(email))
            {
                user.setUserName(userName);
                user.setNickname(nickname);
                user.setPassword(password);
                user.setPhoneNumber(phoneNumber);
                user.setUpdateAt(Instant.now().getEpochSecond());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateByUserName(String userName, String email, String nickname, String password, String phoneNumber) {
        for (User user : userList) {
            if (user.getUserName().equals(userName))
            {
                user.setEmail(email);
                user.setNickname(nickname);
                user.setPassword(password);
                user.setPhoneNumber(phoneNumber);
                user.setUpdateAt(Instant.now().getEpochSecond());
                return true;
            }
        }

        return false;
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
