package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {
    User create(String username, String password);

    User find(UUID userId);

    List<User> findAll();

    User update(UUID id, String username, String password);

    void delete(UUID id);

    Optional<User> findByUsername(String username);

    /**
     * 모든 사용자 데이터를 초기화합니다.
     * 테스트 환경에서 사용됩니다.
     */
    void clear();
}
