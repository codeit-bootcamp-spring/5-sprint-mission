package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    void save(User user); // 유저 저장

    User findById(UUID id); // 조회

    List<User> findAll(); // 전체 조회

    void update(User user); // 수정

    void delete(UUID id); // 삭제

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    Optional<User> findByUserIdAndPassword(String userId, String password);


}
