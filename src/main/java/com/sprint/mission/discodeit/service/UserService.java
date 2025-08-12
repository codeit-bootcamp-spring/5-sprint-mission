package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며, 다중구현이 가능
//CRUD(생성,읽기,모두읽기,수정,삭제) 기능 구현하기


import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {

    //약속
    //다중 구현 가능
    void create(UserCreateRequest request); //저장

    UserResponse findById(UUID id); //조회

    List<UserResponse> findAll(); //리스트에 넣기

    void update(UserUpdateRequest request); //수정

    void delete(UUID id); //삭제
}
