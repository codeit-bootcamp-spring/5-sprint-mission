package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * User 관리 전용 러너 클래스.
 *  - 모든 필드·메서드를 인스턴스 컨텍스트로 유지해 "static‑context" 오류를 제거했습니다.
 */
public class UserRun {
    private final Scanner sc;
    private final UserService userService;

    public UserRun(Scanner sc, UserService userService) {
        this.sc = sc;
        this.userService = userService;
    }

    public void run() {
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

    /* ========== CRUD 로직 ========== */

    private void createUser() {
        System.out.print("이름: ");
        String name = sc.nextLine();
        System.out.print("이메일: ");
        String email = sc.nextLine();
        User newUser = new User(name, email);
        userService.create(newUser);
        System.out.println("등록 완료:\n[이름: " + newUser.getName() + "]\n[이메일: " + newUser.getEmail() + "]");
    }

    private void getUser() {
        System.out.print("정보 조회할 유저 이름: ");
        String name = sc.nextLine().trim();
        User user = userService.get(name); // 이름 기반 메서드가 없다면 추가 구현 필요
        if (user == null) {
            System.out.println("해당 이름의 유저 없음");
            return;
        }
        System.out.println("유저 정보: " + user);
    }

    private void getAllUsers() {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            System.out.println("등록된 유저 없음");
            return;
        }
        System.out.println("전체 유저 목록");
        users.forEach(u -> {
            System.out.println("-------------------------------------------");
            System.out.println(u);
        });
        System.out.println("-------------------------------------------");
    }

    private void updateUser() {
        System.out.print("수정할 유저 이름: ");
        String name = sc.nextLine().trim();
        User user = userService.get(name);
        if (user == null) {
            System.out.println("해당 이름의 유저 없음");
            return;
        }
        System.out.print("새 이름: ");
        String newName = sc.nextLine();
        System.out.print("새 이메일: ");
        String newEmail = sc.nextLine();
        user.update(newName, newEmail);
        userService.update(user);
        System.out.println("수정 완료");
    }

    private void deleteUser() {
        System.out.print("삭제할 유저 이름: ");
        String name = sc.nextLine().trim();
        User user = userService.get(name);
        if (user == null) {
            System.out.println("해당 이름의 유저가 없습니다.");
            return;
        }
        UUID userID = user.getId();
        userService.delete(userID);
        System.out.println("삭제 완료: " + name);
    }
}