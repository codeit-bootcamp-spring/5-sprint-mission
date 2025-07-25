package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Scanner sc = new Scanner(System.in);
    private static final Map<UUID, User> data = new HashMap<>();


    @Override
    public User register(String username, String password) {
        User user = new User(username, password);
        data.put(user.getId(), user);
        System.out.println("회원가입이 완료되었습니다.");
        System.out.println("회원 정보 : " + user);
        return user;
    }

    @Override
    public boolean login() {
        String idInput;
        String pwInput;

        while (true) {
            System.out.print("ID를 입력하세요 : ");
            idInput = sc.nextLine();

            System.out.print("비밀번호를 입력하세요 : ");
            pwInput = sc.nextLine();

            if (idInput.isBlank() || pwInput.isBlank() ||
                    idInput.matches(".*\\s.*") || pwInput.matches(".*\\s.*")) {
                System.out.println("ID와 비밀번호는 공백 없이 한 글자 이상 입력하세요.\n");
            } else {
                break;
            }
        }

        try {
            UUID id = UUID.fromString(idInput.trim());
            User user = data.get(id);
            if (user != null && user.getPassword().equals(pwInput.trim())) {
                System.out.println("로그인에 성공했습니다." + user.getUsername() + "님 반갑습니다!");
                return true;
            } else {
                System.out.println("ID 또는 비밀번호가 일치하지 않아 로그인에 실패했습니다.");
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않은 ID 형식입니다.");
            return false;
        }
    }

    @Override
    public User find(UUID userId) {
        User user = data.get(userId);
        return user;
    }

    @Override
    public User findAll() {
        return null;
    }

    @Override
    public User update(UUID userId, String newPassword) {
        return null;
    }

    @Override
    public boolean delete(UUID userId) {
        return data.remove(userId) != null;
    }
}
