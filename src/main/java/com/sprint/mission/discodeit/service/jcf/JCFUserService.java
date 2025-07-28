package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    public final List<User> data = new ArrayList<>();

    public JCFUserService() {}

    @Override
    public User createUser(String email, String username, String password, String discriminator, UserStatus status) {
        try {
            checkValidate(email, username, password, discriminator, status);
        } catch (IllegalArgumentException e) {
            System.out.println("[Error] " + e.getMessage());
            e.printStackTrace();
        }

        User user = new User(email, username, password, discriminator, status);
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        if(userId == null) {
            throw new NullPointerException("User id is wrong");
        }

        for(User u : data) {
            if(u.getId().equals(userId)) {
                return u;
            }
        }

        throw new NoSuchElementException("User (" + userId + ") not found");
    }

    @Override
    public List<User> findAll() { return data; }

    @Override
    public User update(UUID userId, String email, String username, String password, String discriminator, UserStatus status) throws IllegalArgumentException, NullPointerException {
        if(userId == null) {
            throw new NullPointerException("User id is null.");
        }
        checkValidate(email, username, password, discriminator, status);
        for(User u : data) {
            if(u.getId().equals(userId) && u.getDiscriminator().equals(discriminator)) {
                u.update(email, username, password, status);
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
    public void checkValidate(String email, String username, String password, String discriminator, UserStatus status) {
        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is null or blank.");
        } if(username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is null or blank.");
        } if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is null or blank.");
        } if(discriminator == null || discriminator.isBlank()) {
            throw new IllegalArgumentException("discriminator is null or blank.");
        } if(status == null) {
            throw new IllegalArgumentException("status is null.");
        }
    }
}
