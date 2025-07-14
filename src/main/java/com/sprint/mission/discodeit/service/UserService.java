package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface UserService {
    //생성
    User createUser(String userId, String name, String password);
    //읽기
    User getUser(String userId);
    //모두 읽기
    List<User> getUsers();
    //수정
    boolean updateUserName(String userId, String name);

    boolean updatePassword(String userId, String oldPassword, String newPassword);

    //삭제
    boolean deleteUser(String userId);
}
