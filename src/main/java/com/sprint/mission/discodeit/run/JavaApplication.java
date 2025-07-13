package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.channelEntity.ChannelCategory;
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
    // private User me;
    private User me = new User("1", "1", "1", LocalDate.of(1995, 4, 10), true, "1"); // 테스트용

    private void mainMenu() {
        System.out.println("========== Discodeit ==========");
        userService.registerUser(me); // 테스트용

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
                        System.out.println("프로그램 종료.\n");
                        return;
                    default:
                        System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.\n");
            }
        }
    }

    private void userMenu() {
        whileLoop: while (true) {
            System.out.println("*** 회원 메뉴 ***");
            System.out.println("1. 내 정보 보기");
            System.out.println("2. 이메일 변경");
            System.out.println("3. 별명 변경");
            System.out.println("4. 사용자명 변경");
            System.out.println("5. 비밀번호 변경");
            System.out.println("6. 생년월일 변경");
            System.out.println("7. 이메일로 소식 받기 변경");
            System.out.println("8. 휴대폰 번호 등록/변경");
            System.out.println("9. 친구 목록 보기");
            System.out.println("10. 친구 추가");
            System.out.println("11. 친구 삭제");
            System.out.println("12. 모든 서버 조회");
            System.out.println("13. 서버 만들기");
            System.out.println("14. 서버 삭제");
            System.out.println("15. 서버 참가");
            System.out.println("16. 서버 나가기");
            System.out.println("17. 서버 열기");
            System.out.println("18. 다이렉트 메시지 목록 보기");
            System.out.println("19. 다이렉트 메시지 보내기");
            System.out.println("20. 다이렉트 메시지 보기");
            System.out.println("21. 다이렉트 메시지 수정하기");
            System.out.println("22. 니트로 플랜 구독하기");
            System.out.println("23. 아이템 구매하기");
            System.out.println("24. 계정 삭제하기");
            System.out.println("25. 로그아웃");
            System.out.println("99. 뒤로가기");
            System.out.print("메뉴 번호 선택 : ");

            try {
                int menuNum = Integer.parseInt(sc.nextLine());
                switch (menuNum) {
                    case 1:
                        showMe();
                        continue;
                    case 2:
                        changeEmail();
                        continue;
                    case 3:
                        changeNickname();
                        continue;
                    case 4:
                        changeUsername();
                        continue;
                    case 5:
                        changePassword();
                        continue;
                    case 6:
                        changeBirthDate();
                        continue;
                    case 7:
                        changeIsSubscribedToNewsletter();
                        continue;
                    case 8:
                        changePhoneNumber();
                        continue;
                    case 9:
                        showFriends();
                        continue;
                    case 10:
                        addFriend();
                        continue;
                    case 11:
                        deleteFriend();
                        continue;
                    case 12:
                        showServers();
                        continue;
                    case 13:
                        createServer();
                        continue;
                    case 14:
                        deleteServer();
                        continue;
                    case 15:
                        joinServer();
                        continue;
                    case 16:
                        exitServer();
                        continue;
                    case 17:
                        openServer();
                        continue;
                    case 99:
                        break whileLoop;
                    default:
                        System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.\n");
            }
        }
    }

    private void serverMenu() {
        while (true) {
            System.out.println("=====***** 서버 메뉴 *****=====");
            System.out.println("1. 서버 정보 조회");
            System.out.println("2. 서버 주인 변경");
            System.out.println("3. 공개 여부 변경");
            System.out.println("4. 회원 추방");
            System.out.println("5. 채널들 조회");
            System.out.println("6. 채널 생성");
            System.out.println("7. 채널 수정");
            System.out.println("8. 채널 삭제");
            System.out.println("99. 뒤로가기");
            System.out.print("메뉴 번호 입력: ");

            try {
                int menuNum = Integer.parseInt(sc.nextLine());

                switch (menuNum) {
                    case 99:
                        System.out.println("프로그램 종료.\n");
                        return;
                    default:
                        System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.\n");
            }
        }
    }

    private void register() {
        String email;
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        whileLoop:
        while (true) {
            System.out.print("이메일 : ");
            email = sc.nextLine();
            List<User> users = userService.findAll();
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    System.out.println("중복된 이메일입니다. 다시 입력해주세요.\n");
                    continue whileLoop;
                }
            }
            if (!email.matches(emailRegex)) {
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.\n");
                continue;
            }
            break;
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
                System.out.println("사용자명은 필수입니다.\n");
            }
        }

        String password;
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$";

        while (true) {
            System.out.print("비밀번호 : ");
            password = sc.nextLine();
            if (!password.matches(passwordRegex)) {
                System.out.println("비밀번호는 영문, 숫자, 특수 문자 조합 8자 이상을 입력해 주세요.\n");
                continue;
            }
            break;
        }

        LocalDate birthDate;
        while (true) {
            try {
                System.out.println("생년월일");
                System.out.print("년 : ");
                int year = Integer.parseInt(sc.nextLine());

                System.out.print("월 : ");
                int month = Integer.parseInt(sc.nextLine());

                System.out.print("일 : ");
                int day = Integer.parseInt(sc.nextLine());

                birthDate = LocalDate.of(year, month, day);
                break;
            } catch (NumberFormatException e) {
                System.out.println("숫자 형식이 올바르지 않습니다. 다시 입력해주세요.\n");
            } catch (DateTimeException e) {
                System.out.println("유효하지 않은 날짜입니다. 다시 입력하세요.\n");
            }
        }

        boolean isSubscribedToNewsletter;
        while (true) {
            System.out.print("이메일로 소식 받기(y/n): ");
            String yn = sc.nextLine();
            yn = yn.toLowerCase();
            if (yn.equals("y")) {
                isSubscribedToNewsletter = true;
                break;
            } else if (yn.equals("n")) {
                isSubscribedToNewsletter = false;
                break;
            } else {
                System.out.println("y 또는 n을 입력해주세요.\n");
            }
        }

        User user = new User(email, username, password, birthDate, isSubscribedToNewsletter, nickname);
        boolean result = userService.registerUser(user);
        if (result) {
            System.out.println("성공적으로 회원가입을 완료하였습니다.\n");
            return;
        }
        System.out.println("다시 시도해 주세요.\n");
    }

    private void login() {
        System.out.println("\nx. 뒤로가기");

        while (true) {
            System.out.print("이메일 : ");
            String email = sc.nextLine();
            if (email.equals("x")) {
                return;
            }

            System.out.print("비밀번호 : ");
            String password = sc.nextLine();
            if (password.equals("x")) {
                return;
            }

            User user = userService.loginUser(email, password);
            if (user == null) {
                System.out.println("다시 입력해 주세요.\n");
                continue;
            }
            me = user;
            System.out.println("\n" + user.getUsername() + "님, 환영합니다!\n");
            break;
        }
        userMenu();
    }

    private void findUserByEmail() {
        System.out.print("이메일 : ");
        String email = sc.nextLine().strip();

        User user = userService.findByEmail(email);
        if (user == null) {
            System.out.println("등록된 회원이 없습니다.\n");
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

    private void showMe() {
        System.out.println("\n" + me);
    }

    private void changeEmail() {
        String email;
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        System.out.println("\nx. 뒤로가기");
        System.out.println("현재 이메일: " + me.getEmail());

        whileLoop:
        while (true) {
            System.out.print("변경할 이메일: ");
            email = sc.nextLine().strip();
            if (email.equals("x")) {
                return;
            } else if (email.equals(me.getEmail())) {
                System.out.println("같은 이메일입니다.\n");
                continue;
            }
            List<User> users = userService.findAll();
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    System.out.println("중복된 이메일입니다. 다시 입력해주세요.\n");
                    continue whileLoop;
                }
            }
            if (!email.matches(emailPattern)) {
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.\n");
                continue;
            }
            userService.updateEmail(me.getId(), email);
            me.setEmail(email);
            break;
        }
    }

    private void changeNickname() {
        System.out.println("\nx. 뒤로가기");
        System.out.println("현재 별명: " + me.getNickname());
        System.out.print("변경할 별명: ");
        String nickname = sc.nextLine();
        if (nickname.equals("x")) {
            return;
        }
        userService.updateNickname(me.getId(), nickname);
        me.setNickname(nickname);
    }

    private void changeUsername() {
        System.out.println("\nx. 뒤로가기");
        System.out.println("현재 사용자명: " + me.getUsername());
        System.out.print("변경할 사용자명: ");
        String username = sc.nextLine();
        if (username.equals("x")) {
            return;
        }
        userService.updateUsername(me.getId(), username);
        me.setUsername(username);
    }

    private void changePassword() {
        String password;
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$";

        System.out.println("\nx. 뒤로가기");
        System.out.println("현재 비밀번호: " + me.getPassword());

        while (true) {
            System.out.print("변경할 비밀번호: ");
            password = sc.nextLine();

            if (password.equals("x")) {
                return;
            } else if (password.equals(me.getPassword())) {
                System.out.println("같은 비밀번호입니다.\n");
                continue;
            }

            if (!password.matches(passwordRegex)) {
                System.out.println("비밀번호는 영문, 숫자, 특수 문자 조합 8자 이상을 입력해 주세요.\n");
                continue;
            }
            userService.updatePassword(me.getId(), password);
            me.setPassword(password);
            break;
        }
    }

    private void changeBirthDate() {
        System.out.println("\nx. 뒤로가기");
        System.out.println("현재 생년월일: " + me.getBirthDate());
        LocalDate birthDate;
        while (true) {
            try {
                System.out.println("변경할 생년월일");
                System.out.print("년: ");
                String yearStr = sc.nextLine();
                if (yearStr.equals("x")) {
                    return;
                }
                int year = Integer.parseInt(yearStr);

                System.out.print("월: ");
                String monthStr = sc.nextLine();
                if (monthStr.equals("x")) {
                    return;
                }
                int month = Integer.parseInt(monthStr);

                System.out.print("일: ");
                String dayStr = sc.nextLine();
                if (dayStr.equals("x")) {
                    return;
                }
                int day = Integer.parseInt(dayStr);

                birthDate = LocalDate.of(year, month, day);
                break;
            } catch (NumberFormatException e) {
                System.out.println("숫자 형식이 올바르지 않습니다. 다시 입력해주세요.\n");
            } catch (DateTimeException e) {
                System.out.println("유효하지 않은 날짜입니다. 다시 입력해주세요.\n");
            }
        }
        userService.updateBirthDate(me.getId(), birthDate);
        me.setBirthDate(birthDate);
    }

    private void changeIsSubscribedToNewsletter() {
        System.out.println("\nx. 뒤로가기");
        System.out.println("현재 이메일 소식 수신 여부: " + me.isSubscribedToNewsletter());

        boolean isSubscribedToNewsletter;
        while (true) {
            System.out.print("이메일로 소식 받기(y/n): ");
            String yn = sc.nextLine();
            yn = yn.toLowerCase();
            if (yn.equals("x")) {
                return;
            } else if (yn.equals("y")) {
                isSubscribedToNewsletter = true;
                break;
            } else if (yn.equals("n")) {
                isSubscribedToNewsletter = false;
                break;
            } else {
                System.out.println("y 또는 n을 입력해주세요.\n");
            }
        }
        userService.updateSubscribedToNewsletter(me.getId(), isSubscribedToNewsletter);
        me.setSubscribedToNewsletter(isSubscribedToNewsletter);
    }

    private void changePhoneNumber() {
        System.out.println("\nx. 뒤로가기");
        System.out.println("현재 휴대폰 번호: " + me.getPhoneNumber());
        System.out.print("변경할 휴대폰 번호 : ");
        String phoneNumber = sc.nextLine();
        if (phoneNumber.equals("x")) {
            return;
        }
        userService.updatePhoneNumber(me.getId(), phoneNumber);
        me.setPhoneNumber(phoneNumber);
    }

    private void showFriends() {
        Set<UUID> friends = me.getFriends();
        if (friends == null) {
            System.out.println("\n친구 없음");
        } else {
            System.out.println();
            System.out.print("[");
            friends.forEach(f -> {
                userService.findById(f);
                System.out.print(f + ", ");
            });
            System.out.println("]");
        }
    }

    private void addFriend() {
        System.out.println("\nx. 뒤로가기");
        while (true) {
            System.out.print("추가할 친구의 이메일: ");
            String email = sc.nextLine().strip();

            if (email.equals("x")) {
                return;
            }

            User friend = userService.findByEmail(email);

            if (friend == null) {
                System.out.println("존재하지 않는 이메일입니다.");
                continue;
            }

            if (me.equals(friend)) {
                System.out.println("뭐야 나잖아");
                continue;
            }

            UUID friendId = friend.getId();

            userService.addFriend(me.getId(), friendId);
            me.addFriend(friendId);
            break;
        }
    }

    private void deleteFriend() {
        Set<UUID> friendsId = me.getFriends();

        if (friendsId.isEmpty()) {
            System.out.println("친구 없음");
            return;
        } else {
            System.out.println("친구 목록: " + me.getFriends());
        }

        System.out.println("\nx. 뒤로가기");
        while (true) {
            System.out.print("삭제할 친구의 이메일: ");
            String email = sc.nextLine();

            if (email.equals("x")) {
                return;
            }

            for (UUID friendId : friendsId) {
                if (userService.findById(friendId).getEmail().equals(email)) {
                    userService.removeFriend(me.getId(), friendId);
                    me.removeFriend(friendId);
                    return;
                }
            }

            System.out.println("친구 목록에 존재하지 않는 이메일입니다.\n");
        }
    }

    private void showServers() {
        System.out.println("\n" + serverService.findAll());
    }

    private void createServer() {
        System.out.println("\nx. 뒤로가기");
        while (true) {
            System.out.print("서버 이름: ");
            String name = sc.nextLine();

            if (name.equals("x")) {
                return;
            }

            boolean isPublic;
            while (true) {
                System.out.print("공개 여부(y/n): ");
                String yn = sc.nextLine();
                yn = yn.toLowerCase();
                if (yn.equals("x")) {
                    return;
                } else if (yn.equals("y")) {
                    isPublic = true;
                    break;
                } else if (yn.equals("n")) {
                    isPublic = false;
                    break;
                } else {
                    System.out.println("y 또는 n을 입력해주세요.\n");
                }
            }

            List<Channel> defaultChannels = List.of(
                    new Channel("일반", "채팅 채널", ChannelCategory.CHAT, true),
                    new Channel("일반", "음성 채널", ChannelCategory.VOICE, true)
            );

            Set<UUID> members = new HashSet<>();
            members.add(me.getId());

            Server server = new Server(name, me.getId(), isPublic, members, defaultChannels);

            boolean result = serverService.createServer(server);
            if (result) {
                System.out.println(server.getName() + " 서버가 생성되었습니다.\n");
                return;
            }
            System.out.println("다시 시도해 주세요.\n");
        }
    }

    private void deleteServer() {
        List<Server> servers = serverService.findAll();

        System.out.println("\nx. 뒤로가기");

        if (servers == null) {
            System.out.println("서버 없음");
            return;
        } else {
            System.out.println("서버 목록: ");
            for (int i = 0; i < servers.size(); i++) {
                System.out.println(i + 1 + ". " + servers.get(i));
            }
        }

        while (true) {
            System.out.print("삭제할 서버 번호: ");
            String indexStr = sc.nextLine();

            if (indexStr.equals("x")) {
                return;
            }

            int index;

            try {
                index = Integer.parseInt(indexStr);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.\n");
                continue;
            }

            if (index < 1 || index > servers.size()) {
                System.out.println("유효한 서버 번호를 입력해주세요.\n");
                continue;
            }

            Server server = servers.get(index - 1);

            if (!server.getOwnerId().equals(me.getId())) {
                System.out.println("삭제할 권한이 없습니다.");
                continue;
            }

            serverService.deleteById(server.getId());
            System.out.println(server.getName() + " 서버가 삭제되었습니다.");
            break;
        }
    }

    private void joinServer() {
        List<Server> servers = serverService.findPublicServers();

        System.out.println("\nx. 뒤로가기");

        if (servers == null) {
            System.out.println("서버 없음");
            return;
        } else {
            System.out.println("서버 목록: ");
            for (int i = 0; i < servers.size(); i++) {
                System.out.println(i + 1 + ". " + servers.get(i));
            }
        }

        whileLoop:
        while (true) {
            System.out.print("들어갈 서버 번호: ");
            String indexStr = sc.nextLine();

            if (indexStr.equals("x")) {
                return;
            }

            int index;

            try {
                index = Integer.parseInt(indexStr);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.\n");
                continue;
            }

            if (index < 1 || index > servers.size()) {
                System.out.println("유효한 서버 번호를 입력해주세요.\n");
                continue;
            }

            Server server = servers.get(index - 1);

            Set<UUID> members = server.getMembers();

            for (UUID memberId : members) {
                if (memberId.equals(me.getId())) {
                    System.out.println("이미 들어간 서버입니다.");
                    continue whileLoop;
                }
            }

            serverService.deleteById(server.getId());
            System.out.println(server.getName() + " 서버에 입장했습니다.");
            break;
        }
    }

    private void exitServer() {
        List<Server> servers = serverService.findServersJoined(me.getId());

        System.out.println("\nx. 뒤로가기");

        if (servers == null) {
            System.out.println("서버 없음");
            return;
        } else {
            System.out.println("서버 목록: ");
            for (int i = 0; i < servers.size(); i++) {
                System.out.println(i + 1 + ". " + servers.get(i));
            }
        }

        while (true) {
            System.out.print("나갈 서버 번호: ");
            String indexStr = sc.nextLine();

            if (indexStr.equals("x")) {
                return;
            }

            int index;

            try {
                index = Integer.parseInt(indexStr);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.\n");
                continue;
            }

            if (index < 1 || index > servers.size()) {
                System.out.println("유효한 서버 번호를 입력해주세요.\n");
                continue;
            }

            Server server = servers.get(index - 1);

            serverService.removeMember(server.getId(), me.getId());

            System.out.println(server.getName() + " 서버에서 퇴장했습니다.\n");
            break;
        }
    }

    private void openServer() {
        List<Server> servers = serverService.findServersJoined(me.getId());

        System.out.println("\nx. 뒤로가기");

        if (servers == null) {
            System.out.println("서버 없음");
            return;
        } else {
            System.out.println("서버 목록: ");
            for (int i = 0; i < servers.size(); i++) {
                System.out.println(i + 1 + ". " + servers.get(i));
            }
        }

        while (true) {
            System.out.print("열 서버 번호: ");
            String indexStr = sc.nextLine();

            if (indexStr.equals("x")) {
                return;
            }

            int index;

            try {
                index = Integer.parseInt(indexStr);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.\n");
                continue;
            }

            if (index < 1 || index > servers.size()) {
                System.out.println("유효한 서버 번호를 입력해주세요.\n");
                continue;
            }

            Server server = servers.get(index - 1);
            System.out.println(server.getName() + " 서버를 열었습니다.\n");
            break;
        }

        serverMenu();
    }
}
