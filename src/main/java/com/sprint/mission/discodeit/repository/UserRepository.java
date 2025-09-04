package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  void save(User user);

  User findById(UUID id);

  List<User> findAll();

  void update(User user);

  void delete(UUID id);

  //username : 로그인용 ID
  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  //username : 로그인용 ID
  Optional<User> findByUsernameAndPassword(String username, String password);


}
