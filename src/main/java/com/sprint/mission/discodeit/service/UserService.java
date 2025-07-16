package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.UUID;

public interface UserService {

    // 생성
    void create (User user);

    //읽기
    User find(UUID id);

    //모두 읽기
    ArrayList<User> allFind();

    //업데이트
    void update(UUID id, User user);


    //삭제
    void delete(UUID id);


}
