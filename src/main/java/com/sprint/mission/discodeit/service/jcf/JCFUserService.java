package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Scanner sc = new Scanner(System.in);
    private static final Map<UUID, User> data = new HashMap<>();


    @Override
    public User register(String username, String password) {
        if (username == null || password == null) {
            System.out.println("이름과 비밀번호 중 입력하지 않은 것이 있습니다.");
            return null;
        } else if (username.matches(".*\\s+.*") || password.matches(".*\\s+.*")) {
            System.out.println("이름과 비밀번호는 공백을 포함할 수 없습니다.");
            return null;
        } else {
            User user = new User(username, password);
            data.put(user.getId(), user);
            System.out.println("회원가입이 완료되었습니다.");
            System.out.println("회원 정보 : " + user);
            return user;
        }
    }

    @Override
    public boolean login(UUID userId, String password) {
        User user = data.get(userId);

        if (user == null) {
            return false; // 일치하는 id 없을 경우 false 리턴
        }

        return user.getPassword().equals(password); // 일치하면 true, 불일치하면 false 리턴
    }

    @Override
    public User find(UUID userId) {
        User user = data.get(userId);
        return user;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public User update(UUID userId, String newPassword) {
        if (data.get(userId) == null) {
            System.out.println("올바른 ID가 아닙니다.");
            return null;
        } else if (newPassword == null || newPassword.matches(".*\\s+.*")) {
            System.out.println("비밀번호를 입력하지 않았거나 공백이 포함되었습니다.");
            return null;
        } else {
            User newUser = data.get(userId);
            newUser.update(newUser.getUsername(), newPassword);
            data.put(userId, newUser);
            return newUser;
        }
    }

    @Override
    public boolean delete(UUID userId) {
        return data.remove(userId) != null;
    }
}
