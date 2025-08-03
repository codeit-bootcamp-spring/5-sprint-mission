package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Scanner;

public class UserRun {
    private final Scanner sc;
    private final UserService userService;

    public UserRun(Scanner sc, UserService userService) {
        this.sc = sc;
        this.userService = userService;
    }

    public void run() throws Exception{
        while (true) {
            System.out.println("\n[유저 서비스 메뉴]");
            System.out.println("1. 유저 등록");
            System.out.println("2. 유저 조회");
            System.out.println("3. 전체 유저 조회");
            System.out.println("4. 유저 수정");
            System.out.println("5. 유저 삭제");
            System.out.println("9. 뒤로 가기");
            System.out.print("선택: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
                continue;
            }

            switch (choice) {
                case 1 -> createUser();
                case 2 -> getUser();
                case 3 -> getAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 9 -> {
                    System.out.println("메인 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void createUser() throws Exception{
        System.out.print("이름: ");
        String name = sc.nextLine();
        System.out.print("이메일: ");
        String email = sc.nextLine();

        if (userService.get(email) != null) {
            System.out.println("이미 등록된 이메일입니다.");
            return;
        }
        System.out.print("비밀번호: ");
        String pw = sc.nextLine();
        String maskedPw = pw.length() <= 2 ? pw : pw.substring(0, 2) + "*".repeat(pw.length() - 2);
        User newUser = new User(name, email, pw);
        userService.create(newUser);
        System.out.println("등록 완료:\n[이름: " + newUser.getName() + "]\n[이메일: " + newUser.getEmail() + "]" +
                "\n[비밀번호: " + maskedPw + "]");
    }


    private void getUser() throws Exception{
        System.out.print("정보 조회할 유저 이름: ");
        String name = sc.nextLine().trim();
        User user = userService.get(name);

        if (user == null) {
            System.out.println("해당 이름의 유저 없음");
            return;
        }
        if (!passwordMatch(user)) return;

        System.out.println("유저 정보:\n" + user);
    }

    private void getAllUsers() throws Exception{
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            System.out.println("등록된 유저 없음");
            return;
        }
        System.out.println("\n<전체 유저 목록>");
        for (User u : users) {
            System.out.println("-------------------------------------------");
            System.out.println(u);
        }
        System.out.println("-------------------------------------------");
    }

    private void updateUser() throws Exception{
        System.out.print("수정할 유저 이름: ");
        String name = sc.nextLine().trim();
        User user = userService.get(name);

        if (user == null) {
            System.out.println("해당 이름의 유저 없음");
            return;
        }
        if (!passwordMatch(user)) return;

        System.out.print("새 이름: ");
        String newName = sc.nextLine();
        System.out.print("새 이메일: ");
        String newEmail = sc.nextLine();
        System.out.print("새 비밀번호: ");
        String newPassword = sc.nextLine();

        user.update(newName, newEmail, newPassword);
        userService.update(user);
        System.out.println("수정 완료");
    }

    private void deleteUser() throws Exception{
        System.out.print("삭제할 유저 이름: ");
        String name = sc.nextLine().trim();
        User user = userService.get(name);

        if (user == null) {
            System.out.println("해당 이름의 유저가 없습니다.");
            return;
        }
        if (!passwordMatch(user)) return;

        userService.delete(user.getId());
        System.out.println("삭제 완료: " + name);
    }

    /* ===== 비밀번호 확인 유틸 ===== */
    private boolean passwordMatch(User user) {
        System.out.print("비밀번호 확인: ");
        String pw = sc.nextLine();
        if (!user.getPassword().equals(pw)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false;
        }
        return true;
    }
}
