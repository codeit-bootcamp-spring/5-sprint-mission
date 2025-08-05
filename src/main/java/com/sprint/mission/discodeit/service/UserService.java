package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface UserService {
    //생성
    User create(String userId, String name, String password);
    //읽기
    User get(UUID id);
    //모두 읽기
    List<User> getAll();
    //수정
    User updateUserName(UUID id, String name);

    User updatePassword(UUID id, String oldPassword, String newPassword);

    //삭제
    void delete(UUID id);
}
