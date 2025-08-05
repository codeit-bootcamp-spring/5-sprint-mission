package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public static void main(String[] args) {
        UserRepository ur = new JCFUserRepository();
        User user1 = new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);
        ur.save(user1);
        ur.save(user2);
        ur.save(user2);
        ur.save(user3);
        ur.save(user4);
        ur.save(user5);
        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());


        System.out.println("The number of users : " + ur.count());
        System.out.println("User List : ");
        List<User> users = ur.findAll();
        users.forEach(System.out::println);

        User userFound1 = ur.findById(user1.getId()).get();
        System.out.println("Find user1: " + userFound1.toString());
        try {
            User userFound3 = ur.findById(user3.getId()).get();
            System.out.println("Find user3: " + userFound3.toString());
            User deletedUser4 = ur.delete(user4.getId());
            System.out.println("Delete user4: " + deletedUser4.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.putIfAbsent(user.getId(), user);

        return data.get(user.getId());
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = data.get(id);

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public long count() {
        return (long) data.size();
    }

    @Override
    public User delete(UUID id) {
        User user = null;
        if (existsById(id)) {
            user = data.remove(id);
        }
        return user;
    }

    @Override
    public boolean existsById(UUID id) {
        if (data.containsKey(id)) {
            return true;
        }
        return false;
    }
}
