package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username); // sprint3 추가
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
    boolean existsByEmail(String email); // sprint3 추가
    boolean existsByUsername(String username); // sprint3 추가


}
