package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class UserTest {
    Scanner sc = new Scanner(System.in);
    JCFUserService userService = new JCFUserService();

    public void userMenu() {
        while (true) {
            System.out.println("----------------------------");
            System.out.println("1. 등록");
            System.out.println("2. 조회");
            System.out.println("3. 수정");
            System.out.println("4. 삭제");
            System.out.println("5. 종료");
            System.out.print("번호: ");
            int num = sc.nextInt();

            switch (num) {
                case 1:
                    create();
                    break;
                case 2:
                    read();
                    break;
                case 3:
                    update();
                    break;
                case 4:
                    delete();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("잘못 입력하셨습니다.");
                    break;
            }
        }

    }

    public void create() {
        System.out.println("----------------------------");
        UUID id = UUID.randomUUID();
        System.out.print("이름: ");
        String username = sc.next();
        sc.nextLine();
        System.out.print("비밀번호: ");
        String password = sc.next();
        sc.nextLine();
        System.out.print("나이: ");
        int age = sc.nextInt();
        System.out.print("메일: ");
        String email = sc.next();
        sc.nextLine();

        User user = userService.createUser(id, username, password, age, email);
        System.out.println("생성 완료! " + user);
    }

    public void read() {
        System.out.println("----------------------------");
        System.out.println("1. 읽기");
        System.out.println("2. 모두 읽기");
        System.out.println("3. 메뉴로 돌아가기");
        System.out.print("번호: ");
        int num = sc.nextInt();

        switch (num) {
            case 1:
                readOne();
                break;
            case 2:
                readAll();
                break;
            case 3:
                break;
            default:
                System.out.println("존재하지 않는 메뉴입니다.");
                break;
        }
    }

    public void readOne() {
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;

        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        User user = userService.readUser(id);

        if(user != null) {
            System.out.println(user.getId() + " " + user.getUsername() + " " + user.getPassword()
                    + " " + user.getAge() + " " + user.getEmail());
        } else {
            System.out.println("존재하지 않는 회원입니다.");
        }
    }

    public void readAll() {
        List<User> userList = userService.readAllUsers();

        if (userList != null) {
            for (User user : userList) {
                System.out.println(user.getId() + " " + user.getUsername() + " " + user.getPassword()
                        + " " + user.getAge() + " " + user.getEmail());
            }
        } else {
            System.out.println("등록된 회원이 없습니다.");
        }
    }

    public void update() {
        System.out.println("----------------------------");
        System.out.println("1. 이름 변경");
        System.out.println("2. 비밀번호 변경");
        System.out.println("3. 이메일 변경");
        System.out.println("4. 메뉴로 돌아가기");
        System.out.print("번호: ");
        int num = sc.nextInt();

        switch (num) {
            case 1:
                nameUpdate();
                break;
            case 2:
                passwordUpadte();
                break;
            case 3:
                emailUpdate();
                break;
            case 4:
                break;
            default:
                System.out.println("존재하지 않는 메뉴입니다.");
                break;
        }
    }

    public void nameUpdate() {
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;
        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        System.out.print("변경 이름: ");
        String username = sc.next();
        sc.nextLine();

        User user = userService.updateUsername(id, username);

        if (user != null) {
            System.out.println("변경 완료! " + user);
        } else {
            System.out.println("존재하지 않는 회원입니다.");
        }
    }

    public void passwordUpadte() {
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;
        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        System.out.print("변경 비밀번호: ");
        String password = sc.next();
        sc.nextLine();

        User user = userService.updatePassword(id, password);

        if (user != null) {
            System.out.println("변경 완료! " + user);
        } else {
            System.out.println("존재하지 않는 회원입니다.");
        }
    }

    public void emailUpdate() {
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;
        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        System.out.print("변경 이메일: ");
        String email = sc.next();
        sc.nextLine();

        User user = userService.updateEmail(id, email);

        if (user != null) {
            System.out.println("변경 완료! " + user);
        } else {
            System.out.println("존재하지 않는 회원입니다.");
        }
    }

    public void delete() {
        System.out.println("----------------------------");
        System.out.print("ID: ");
        String str = sc.next();
        sc.nextLine();

        UUID id;

        try {
            id = UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않습니다.");
            return;
        }

        if (userService.deleteUser(id)) {
            System.out.println("삭제되었습니다.");
        } else {
            System.out.println("존재하지 않는 회원입니다.");
        }
    }


    public static void main(String[] args) {
        new UserTest().userMenu();
    }
}
