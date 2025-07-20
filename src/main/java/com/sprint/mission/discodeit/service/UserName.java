package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.model.User;
import java.util.List;

public interface UserService {
    void create(User user);
    User read(Long id);
    List<User> readAll();
    void update(Long id, User user);
    void delete(Long id);
}