package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuView {
    private final Scanner sc = new Scanner(System.in);

    public void mainMenu() {
        while (true) {
            System.out.print("\n===== 메뉴 =====\n1. 로그인\n2. 회원가입\n9. 프로그램 종료\n");
            System.out.print("원하시는 메뉴의 번호를 입력하세요 : ");

            try {
                int menuChoice = sc.nextInt();
                switch (menuChoice) {
                    case 1: {
                        UserService loginUser = new JCFUserService();
                        loginUser.login();
                        if (loginUser.login()) {
                            // loginUser.register();
                        } else {
                            break;
                        }
                    }
                    case 2: {
                        UserService regi = new JCFUserService();
                        regi.register();
                        break;
                    }
                    case 9: {
                        System.out.println("프로그램을 종료합니다.");
                        return;
                    }
                    default: {
                        System.out.println();
                        System.out.println("올바르지 않은 번호입니다. 다시 선택해주세요.");
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("번호만 숫자로 입력해주세요.");
                sc.nextLine();
            }
        }
    }
}
