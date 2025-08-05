package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserRepository {
    void save(User user) throws IOException;
    User findById(UUID id) throws IOException, ClassNotFoundException;
    User findByName(String name) throws IOException, ClassNotFoundException;
    List<User> findAll() throws IOException, ClassNotFoundException;
    void update(User user) throws IOException;
    void delete(UUID id) throws IOException;
}
