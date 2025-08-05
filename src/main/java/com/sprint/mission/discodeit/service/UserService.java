package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    void create(User user) throws IOException;
    User get(UUID id) throws IOException, ClassNotFoundException;
    User get(String name) throws IOException, ClassNotFoundException;
    List<User> getAll() throws IOException, ClassNotFoundException;
    void update(User user) throws IOException;
    void delete(UUID id) throws IOException;
}
