package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 추가
    User insert(String name, String email, String password);

    // 사용자 조회
    User selectOne(UUID userId);

    // 사용자 전체 조회
    List<User> selectAll();

    // 사용자 수정
    User update(UUID userId, String name, String email, String password);

    // 사용자 삭제
    boolean delete(UUID userId);
}
