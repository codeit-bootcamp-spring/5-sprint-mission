package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void register(User user);            //사용자 등록
    User findById(UUID id);              //사용자 아이디로 검색
    List<User> findAll();                //모든 사용자 조회
    boolean update(User user);           //사용자 수정
    boolean deleteUser(UUID id);         //사용자 삭제
}
