package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface UserService {
    //생성
    User create(String userId, String name, String password);
    //읽기
    User get(String userId);
    //모두 읽기
    List<User> getAll();
    //수정
    boolean updateUserName(String userId, String name);

    boolean updatePassword(String userId, String oldPassword, String newPassword);

    //삭제
    boolean delete(String userId);
}
