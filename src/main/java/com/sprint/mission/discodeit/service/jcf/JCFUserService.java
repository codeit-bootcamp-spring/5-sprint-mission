package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserDTO;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    public final List<User> data = new ArrayList<>();

    public static void main(String[] args) {
        JCFUserService jcfu = new JCFUserService();
        System.out.println("----- user create -----");
        User user1 = jcfu.createUser("kkk@kkk.com", "james", "1234", "#4756", "online");
        User user2 = jcfu.createUser("jjj@jjj.com", "john", "3454", "#3132", "offline");
        User user3 = jcfu.createUser("sss@sss.com", "kim", "1133", "#5666", "online");
        User user4 = jcfu.createUser("ttt@ttk.com", "park", "1564", "#4786", "afk");
        User user5 = jcfu.createUser("kyy@yyk.com", "elis", "1777", "#9876", "good");

        System.out.println(user1.toString());
        System.out.println(user2.toString());
        System.out.println(user3.toString());
        System.out.println(user4.toString());
        System.out.println(user5.toString());


        User findU1 = null;
        try {
            System.out.println("----- user find -----");
            findU1 = jcfu.findById(user3.getId());
            System.out.println(findU1.toString());
//            User findU2 = jcfu.findById(UUID.randomUUID());
//            System.out.println(findU2.toString());
            System.out.println("----- all users find -----");
            List<User> allUsers =  jcfu.findAll();
            allUsers.forEach(System.out::println);

            System.out.println("----- user delete -----");
            User delU1 = jcfu.deleteById(findU1.getId());
            System.out.println(delU1.toString());

            System.out.println("----- user update -----");
            UserDTO userDTO1 = new UserDTO(user4.getId(), "jjj@jjj.net", "ryu", "9999", "#5567", "null");
            System.out.println(userDTO1.toString());
            User updateU1 = jcfu.update(userDTO1);
            System.out.println(updateU1.toString());

        } catch(NullPointerException | IllegalArgumentException e) {
            System.out.println("[Error] " + e.getMessage());
        }


    }

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
        throw new IllegalArgumentException("User id is wrong.");
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
