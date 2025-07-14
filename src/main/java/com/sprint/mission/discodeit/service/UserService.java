package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    boolean createUser(String email, String userName, String nickname, String password, String phoneNumber);
    User findById(UUID id);
    User findByEmail(String email);
    User findByUserName(String userName);
    List<User> findByNickName(String nickname);
    List<User> findAllUsers();
    boolean updateByEmail(String email, String userName, String nickname, String password, String phoneNumber);
    boolean updateByUserName(String userName, String email, String nickname, String password, String phoneNumber);
    boolean deleteByEmail(String email);
    boolean deleteByUserName(String userName);
}
