package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;

public class JavaApplication {
    public static void main(String[] args) {
        new JavaApplication().mainMenu();
    }

    private final Scanner sc = new Scanner(System.in);
    private final JCFUserService userService = JCFUserService.getInstance();
    private final JCFMessageService messageService = JCFMessageService.getInstance();
    private final JCFChannelService channelService = JCFChannelService.getInstance();
    private final JCFServerService serverService = JCFServerService.getInstance();
    private final JCFSurveyService surveyService = JCFSurveyService.getInstance();
    private User me;

    private void mainMenu() {
        System.out.println("========== Discodeit ==========");

        while (true) {
            System.out.println("=====***** 메인 메뉴 *****=====");
            System.out.println("1. 회원가입");
            System.out.println("2. 로그인");
            System.out.println("3. 이메일로 회원 찾기");
            System.out.println("4. 모든 회원 보기");
            System.out.println("9. 종료");
            System.out.print("메뉴 번호 입력 : ");

            try {
                int menuNum = Integer.parseInt(sc.nextLine());

                switch (menuNum) {
                    case 1:
                        register();
                        continue;
                    case 2:
                        login();
                        continue;
                    case 3:
                        findUserByEmail();
                        continue;
                    case 4:
                        showAllUsers();
                        continue;
                    case 9:
                        System.out.println("프로그램 종료.");
                        return;
                    default:
                        System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
                System.out.println();
            }
        }
    }

    public void afterLoginMenu() {
        while (true) {
            System.out.println("*** 회원 메뉴 ***");
            System.out.println("1. 내 정보 보기");
            System.out.println("2. 이메일 변경");
            System.out.println("3. 별명 변경");
            System.out.println("4. 사용자명 변경");
            System.out.println("5. 비밀번호 변경");
            System.out.println("6. 생년월일 변경");
            System.out.println("7. 이메일로 소식 받기 변경");
            System.out.println("8. 휴대폰 번호 등록/변경");
            System.out.println("9. 친구 추가");
            System.out.println("10. 친구 삭제");
            System.out.println("11. 친구 목록 보기");
            System.out.println("12. 서버 만들기");
            System.out.println("13. 서버 삭제");
            System.out.println("14. 서버 참가");
            System.out.println("15. 서버 나가기");
            System.out.println("16. 다이렉트 메시지 목록 보기");
            System.out.println("17. 다이렉트 메시지 보내기");
            System.out.println("18. 다이렉트 메시지 보기");
            System.out.println("19. 다이렉트 메시지 수정하기");
            System.out.println("20. 니트로 플랜 구독하기");
            System.out.println("21. 아이템 구매하기");
            System.out.println("22. 계정 삭제하기");
            System.out.println("23. 로그아웃");
            System.out.print("메뉴 번호 선택 : ");

            int menuNum = Integer.parseInt(sc.nextLine());
            System.out.println(menuNum);
        }
    }


    private void register() {
        String email;
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        whileLoop: while (true) {
            System.out.print("이메일 : ");
            email = sc.nextLine();
            List<User> users = userService.findAll();
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    System.out.println("중복된 이메일입니다. 다시 입력해주세요.");
                    continue whileLoop;
                }
            }
            if (email.matches(emailPattern)) {
                break;
            } else {
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
            }
        }

        System.out.print("별명(선택) : ");
        String nickname = sc.nextLine();

        String username;
        while (true) {
            System.out.print("사용자명 : ");
            username = sc.nextLine();
            if (!username.isEmpty()) {
                break;
            } else {
                System.out.println("사용자명은 필수입니다.");
            }
        }

        String password;
        while (true) {
            System.out.print("비밀번호 : ");
            password = sc.nextLine();
            if (!password.isEmpty()) {
                break;
            } else {
                System.out.println("비밀번호는 필수입니다.");
            }
        }

        LocalDate birthdate;
        while (true) {
            try {
                System.out.println("생년월일");
                System.out.print("년 : ");
                int year = Integer.parseInt(sc.nextLine());

                System.out.print("월 : ");
                int month = Integer.parseInt(sc.nextLine());

                System.out.print("일 : ");
                int day = Integer.parseInt(sc.nextLine());

                birthdate = LocalDate.of(year, month, day);
                break;
            } catch (NumberFormatException e) {
                System.out.println("숫자 형식이 올바르지 않습니다. 다시 입력해주세요.");
            } catch (DateTimeException e) {
                System.out.println("유효하지 않은 날짜입니다. 다시 입력하세요.");
            }
        }

        boolean isSubscribedToNewsletter;
        while (true) {
            System.out.print("이메일로 소식 받기(y/n) : ");
            String yn = sc.nextLine();
            yn = yn.toLowerCase();
            if (yn.equals("y")) {
                isSubscribedToNewsletter = true;
                break;
            } else if (yn.equals("n")) {
                isSubscribedToNewsletter = false;
                break;
            } else {
                System.out.println("y 또는 n을 입력해주세요.");
            }
        }

        User user = new User(email, nickname, username, password, birthdate, isSubscribedToNewsletter);
        boolean result = userService.registerUser(user);
        if (result) {
            System.out.println("성공적으로 회원가입을 완료하였습니다.");
            return;
        }
        System.out.println("다시 시도해 주세요.");
    }

    private void login() {
        while (true) {
            System.out.print("이메일 : ");
            String email = sc.nextLine();

            System.out.print("비밀번호 : ");
            String password = sc.nextLine();

            User user = userService.loginUser(email, password);
            if (user == null) {
                System.out.println("다시 입력해 주세요.");
                continue;
            }
            me = user;
            System.out.println(user.getUsername() + "님, 환영합니다!");
            break;
        }
        afterLoginMenu();
    }

    private void findUserByEmail() {
        System.out.print("이메일 : ");
        String email = sc.nextLine().strip();

        User user = userService.findByEmail(email);
        if (user == null) {
            System.out.println("등록된 회원이 없습니다.");
        } else {
            System.out.println(user);
        }
    }

    private void showAllUsers() {
        List<User> users = userService.findAll();
        for (User user : users) {
            System.out.println(user);
        }
    }
}
