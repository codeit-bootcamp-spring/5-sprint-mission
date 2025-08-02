package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며, 다중구현이 가능
//CRUD(생성,읽기,모두읽기,수정,삭제) 기능 구현하기


import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    //약속
    //다중 구현 가능
    void create(User user); //저장

    User findById(UUID id); //조회

    List<User> findAll(); //리스트에 넣기

    void update(User user); //수정

    void delete(UUID id); //삭제
}
