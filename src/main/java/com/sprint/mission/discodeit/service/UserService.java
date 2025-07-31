package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String nickname);
    User findById(UUID id); //유저 조회
    List<User> findAll(); //전체 유저 목록
    void update(UUID id, String newNickname);
    void delete(UUID id);
}