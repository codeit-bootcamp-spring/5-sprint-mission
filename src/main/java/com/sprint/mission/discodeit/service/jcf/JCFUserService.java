package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserDTO;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    public final List<User> data = new ArrayList<>();

    public JCFUserService() {}

    @Override
    public User createUser(String email, String username, String password, String discriminator, String status) {
        try {
            checkValidate(email, username, password, discriminator, status);
        } catch (IllegalArgumentException e) {
            System.out.println("[Error] " + e.getMessage());
        }

        User user = new User(email, username, password, discriminator, status);
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID userId) throws NullPointerException, IllegalArgumentException {
        if(userId == null) {
            throw new NullPointerException("User id is wrong");
        }

        for(User u : data) {
            if(u.getId().equals(userId)) {
                return u;
            }
        }

        throw new IllegalArgumentException("User not found");
    }

    @Override
    public List<User> findAll() { return data; }

    @Override
    public User update(UserDTO userDTO) throws IllegalArgumentException, NullPointerException {
        if(userDTO.getId() == null) {
            throw new NullPointerException("User id is null.");
        }
        checkValidate(userDTO.getEmail(), userDTO.getUsername(), userDTO.getPassword(), userDTO.getDiscriminator(), userDTO.getStatus());
        for(User u : data) {
            if(u.getId().equals(userDTO.getId())) {
                u.update(userDTO);
                return u;
            }
        }

        throw new IllegalArgumentException("User not found");
    }

    @Override
    public User deleteById(UUID userId) throws IllegalArgumentException, NullPointerException {
        if(userId == null) {
            throw new NullPointerException("User id is null.");
        }

        Iterator<User> iter = data.iterator();
        while(iter.hasNext()) {
            User user = iter.next();
            if(user.getId().equals(userId)) {
                data.remove(user);
                return user;
            }
        }
        throw new IllegalArgumentException("User does not already exist.");
    }

    @Override
    public UserDTO createUserDTO(UUID userId, String email, String username, String password, String discriminator, String status) {
        if(userId == null) {
            throw new NullPointerException("User id is wrong");
        }
        checkValidate(email, username, password, discriminator, status);
        return new UserDTO(userId, email, username, password, discriminator, status);
    }

    @Override
    public void checkValidate(String email, String username, String password, String discriminator, String status) {
        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is null or blank");
        } if(username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is null or blank ");
        } if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is null or blank");
        } if(discriminator == null || discriminator.isBlank()) {
            throw new IllegalArgumentException("discriminator is null or blank");
        } if(status == null || status.isBlank()) {
            throw new IllegalArgumentException("status is null or blank");
        }
    }
}
