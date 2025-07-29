package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    List<User> findAll();
    User findById(UUID id);
    List<User> findByName(String name);
    User update(UUID id, String name);
    boolean delete(UUID id);
}
