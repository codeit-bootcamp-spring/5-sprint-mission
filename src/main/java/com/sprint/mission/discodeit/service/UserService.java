package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public User createUser(String email, String username, String password, String discriminator, String status);
    public User findById(UUID userId);
    List<User> findAll();
    User update(UserDTO userDTO);
    User deleteById(UUID userId);
    UserDTO createUserDTO(UUID userId, String email, String username, String password, String discriminator, String status);
    void checkValidate(String email, String username, String password, String discriminator, String status);
}
