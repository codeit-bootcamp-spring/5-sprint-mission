package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import com.sprint.mission.discodeit.service.jcf.JcfChannelService;
import com.sprint.mission.discodeit.service.jcf.JcfGuildService;
import com.sprint.mission.discodeit.service.jcf.JcfMessageService;
import com.sprint.mission.discodeit.service.jcf.JcfSurveyService;
import com.sprint.mission.discodeit.service.jcf.JcfUserService;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaApplication {
  private final Scanner sc = new Scanner(System.in);
  private final JcfUserService userService = JcfUserService.getInstance();
  private final JcfMessageService messageService = JcfMessageService.getInstance();
  private final JcfChannelService channelService = JcfChannelService.getInstance();
  private final JcfGuildService guildService = JcfGuildService.getInstance();
  private final JcfSurveyService surveyService = JcfSurveyService.getInstance();
  private User me;

  public static void main(String[] args) {
    new JavaApplication().mainMenu();
  }

  private void mainMenu() {
    userService.registerUser(
        new User("a@a.aa", "1", "1111aaaa", LocalDate.of(1995, 4, 10), true, "1")); // 테스트용
    userService.registerUser(
        new User("b@b.bb", "2", "2222bbbb", LocalDate.of(1995, 4, 11), false, "2")); // 테스트용
    System.out.println("\n========== Discodeit ==========");

    while (true) {
      System.out.println("=====***** 메인 메뉴 *****=====");
      System.out.println("1. 회원가입");
      System.out.println("2. 로그인");
      System.out.println("3. 이메일로 회원 조회");
      System.out.println("4. 모든 회원 조회");
      System.out.println("9. 종료");
      System.out.print("메뉴 번호 입력 : ");

      try {
        int menuNum = Integer.parseInt(sc.nextLine());
        switch (menuNum) {
          case 1:
            register();
            break;
          case 2:
            login();
            break;
          case 3:
            findUserByEmail();
            break;
          case 4:
            showAllUsers();
            break;
          case 9:
            System.out.println("프로그램 종료");
            sc.close();
            return;
          default:
            System.out.println("다시 입력해주세요.");
        }
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
    }
  }

  private void userMenu() {
    label:
    while (true) {
      System.out.println();
      System.out.println("=====***** 회원 메뉴 *****=====");
      System.out.println("1. 프로필 편집");
      System.out.println("2. 친구 목록 편집");
      System.out.println("3. 서버 목록 편집");
      System.out.println("4. 다이렉트 메시지 편집");
      System.out.println("9. 로그아웃");
      System.out.print("메뉴 번호 입력 : ");

      try {
        int menuNum = Integer.parseInt(sc.nextLine());
        switch (menuNum) {
          case 1:
            editProfileMenu();
            break;
          case 2:
            editFriendMenu();
            break;
          case 3:
            editGuildMenu();
            break;
          case 4:
            editDirectMessageMenu();
            break;
          case 9:
            logout();
            break label;
          default:
            System.out.println("다시 입력해주세요.");
        }
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
      if (me == null) {
        break;
      }
    }
  }

  private void editProfileMenu() {
    label:
    while (true) {
      System.out.println();
      System.out.println(me);
      System.out.println("=====***** 프로필 편집 메뉴 *****=====");
      System.out.println("1. 이메일 변경");
      System.out.println("2. 별명 변경");
      System.out.println("3. 사용자명 변경");
      System.out.println("4. 비밀번호 변경");
      System.out.println("5. 생년월일 변경");
      System.out.println("6. 이메일로 소식 받기 변경");
      System.out.println("7. 휴대폰 번호 등록/변경");
      System.out.println("8. 회원 탈퇴");
      System.out.println("9. 뒤로가기");
      System.out.print("메뉴 번호 입력 : ");

      try {
        int menuNum = Integer.parseInt(sc.nextLine());
        switch (menuNum) {
          case 1:
            changeEmail();
            break;
          case 2:
            changeNickname();
            break;
          case 3:
            changeUsername();
            break;
          case 4:
            changePassword();
            break;
          case 5:
            changeBirthDate();
            break;
          case 6:
            changeIsSubscribedToNewsletter();
            break;
          case 7:
            changePhoneNumber();
            break;
          case 8:
            deleteAccount();
            break;
          case 9:
            break label;
          default:
            System.out.println("다시 입력해주세요.");
        }
        if (me == null) {
          break;
        }
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
    }
  }

  private void editFriendMenu() {
    label:
    while (true) {
      showFriends();
      System.out.println("=====***** 친구 목록 편집 메뉴 *****=====");
      System.out.println("1. 친구 추가");
      System.out.println("2. 친구 삭제");
      System.out.println("9. 뒤로가기");
      System.out.print("메뉴 번호 입력 : ");

      try {
        int menuNum = Integer.parseInt(sc.nextLine());
        switch (menuNum) {
          case 1:
            addFriend();
            break;
          case 2:
            deleteFriend();
            break;
          case 9:
            break label;
          default:
            System.out.println("다시 입력해주세요.");
        }
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
    }
  }

  private void editGuildMenu() {
    label:
    while (true) {
      System.out.println("=====***** 서버 목록 편집 메뉴*****=====");
      System.out.println("1. 모든 서버 조회");
      System.out.println("2. 서버 만들기");
      System.out.println("3. 서버 삭제");
      System.out.println("4. 서버 참가");
      System.out.println("5. 서버 나가기");
      System.out.println("6. 서버 열기");
      System.out.println("9. 뒤로가기");
      System.out.print("메뉴 번호 입력 : ");

      try {
        int menuNum = Integer.parseInt(sc.nextLine());
        switch (menuNum) {
          case 1:
            showGuilds();
            break;
          case 2:
            createGuild();
            break;
          case 3:
            deleteGuild();
            break;
          case 4:
            joinGuild();
            break;
          case 5:
            exitGuild();
            break;
          case 6:
            openGuild();
            break;
          case 9:
            break label;
          default:
            System.out.println("다시 입력해주세요.");
        }
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
    }
  }

  private void guildMenu() {
    label:
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
      System.out.println("9. 뒤로가기");
      System.out.print("메뉴 번호 입력: ");

      try {
        int menuNum = Integer.parseInt(sc.nextLine());

        switch (menuNum) {
          case 1:
            continue;
          case 9:
            break label;
          default:
            System.out.println("다시 입력해주세요.");
        }
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
    }
  }

  private void editDirectMessageMenu() {
    label:
    while (true) {
      System.out.println("=====***** 다이렉트 메시지 편집 메뉴*****=====");
      System.out.println("1. 다이렉트 메시지 목록 보기");
      System.out.println("2. 다이렉트 메시지 보내기");
      System.out.println("3. 다이렉트 메시지 보기");
      System.out.println("4. 다이렉트 메시지 수정하기");
      System.out.println("9. 뒤로가기");
      System.out.print("메뉴 번호 입력: ");

      try {
        int menuNum = Integer.parseInt(sc.nextLine());

        switch (menuNum) {
          case 1:
            continue;
          case 9:
            break label;
          default:
            System.out.println("다시 입력해주세요.");
        }
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
    }
  }

  private void register() {
    String email;
    String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    while (true) {
      System.out.print("이메일 : ");
      email = sc.nextLine().strip();
      if (!email.matches(emailPattern)) {
        System.out.println("잘못된 형식입니다. 다시 입력해주세요.\n");
        continue;
      }
      User user = userService.findByEmail(email);
      if (user == null) {
        break;
      }
      System.out.println("중복된 이메일입니다. 다시 입력해주세요.\n");
    }

    System.out.print("별명(선택) : ");
    final String nickname = sc.nextLine().strip();
    String username;
    while (true) {
      System.out.print("사용자명 : ");
      username = sc.nextLine().strip();
      if (!username.isEmpty()) {
        break;
      }
      System.out.println("사용자명은 필수입니다.\n");
    }

    String password;
    String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$";
    while (true) {
      System.out.print("비밀번호 : ");
      password = sc.nextLine().strip();
      if (password.matches(passwordPattern)) {
        break;
      }
      System.out.println("비밀번호는 영문, 숫자, 특수 문자 조합 8자 이상을 입력해 주세요.");
    }

    LocalDate birthDate;
    while (true) {
      try {
        System.out.println("생년월일");
        System.out.print("년 : ");
        int year = Integer.parseInt(sc.nextLine().strip());
        System.out.print("월 : ");
        int month = Integer.parseInt(sc.nextLine().strip());
        System.out.print("일 : ");
        int day = Integer.parseInt(sc.nextLine().strip());
        birthDate = LocalDate.of(year, month, day);
        break;
      } catch (NumberFormatException e) {
        System.out.println("숫자 형식이 올바르지 않습니다. 다시 입력해주세요.");
      } catch (DateTimeException e) {
        System.out.println("유효하지 않은 날짜입니다. 다시 입력해주세요.");
      }
    }

    boolean isSubscribedToNewsletter;
    while (true) {
      System.out.print("이메일로 소식 받기(y/n): ");
      String yn = sc.nextLine().strip().toLowerCase();
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

    User user =
        userService.registerUser(
            new User(email, username, password, birthDate, isSubscribedToNewsletter, nickname));
    if (user != null) {
      System.out.println("성공적으로 회원가입을 완료하였습니다.");
    } else {
      System.out.println("다시 시도해 주세요.");
    }
  }

  private void login() {
    System.out.println("\nx. 뒤로가기");

    while (true) {
      System.out.print("이메일 : ");
      String email = sc.nextLine().strip();
      if (email.equals("x")) {
        return;
      }

      System.out.print("비밀번호 : ");
      String password = sc.nextLine().strip();
      if (password.equals("x")) {
        return;
      }

      User user = userService.login(email, password);
      if (user == null) {
        System.out.println("다시 입력해 주세요.");
        continue;
      }
      me = user;
      System.out.println(user.getUsername() + "님, 환영합니다!");
      break;
    }
    userMenu();
  }

  private void logout() {
    if (me != null) {
      userService.logout(me.getId());
    }
    me = null;
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
    users.forEach(System.out::println);
  }

  private void changeEmail() {
    String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 이메일: " + me.getEmail());

    while (true) {
      System.out.print("변경할 이메일: ");
      String email = sc.nextLine().strip();

      if (email.equals("x")) {
        return;
      }
      if (!email.matches(emailPattern)) {
        System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
        continue;
      }
      if (email.equals(me.getEmail())) {
        System.out.println("같은 이메일입니다.");
        continue;
      }
      User duplicatedUser = userService.findByEmail(email);
      if (duplicatedUser != null) {
        System.out.println("중복된 이메일입니다. 다시 입력해주세요.");
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
    String nickname = sc.nextLine().strip();
    if (nickname.equals("x")) {
      return;
    }

    userService.updateNickname(me.getId(), nickname);
    me.setNickname(nickname);
  }

  private void changeUsername() {
    System.out.println("\nx. 뒤로가기");
    String oldUsername = me.getNickname();
    String newUsername;

    while (true) {
      System.out.println("현재 사용자명: " + oldUsername);
      System.out.print("변경할 사용자명 : ");
      newUsername = sc.nextLine().strip();
      if (!newUsername.isEmpty()) {
        break;
      }
      System.out.println("사용자명은 필수입니다.\n");
    }
    userService.updateUsername(me.getId(), newUsername);
    me.setUsername(newUsername);
  }

  private void changePassword() {
    String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$";

    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 비밀번호: " + me.getPassword());

    while (true) {
      System.out.print("변경할 비밀번호: ");
      String password = sc.nextLine().strip();

      if (password.equals("x")) {
        return;
      }
      if (!password.matches(passwordPattern)) {
        System.out.println("비밀번호는 영문, 숫자, 특수 문자 조합 8자 이상을 입력해 주세요.");
        continue;
      }
      if (password.equals(me.getPassword())) {
        System.out.println("같은 비밀번호입니다.");
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
        final int year = Integer.parseInt(yearStr);

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
        System.out.println("숫자 형식이 올바르지 않습니다. 다시 입력해주세요.");
      } catch (DateTimeException e) {
        System.out.println("유효하지 않은 날짜입니다. 다시 입력해주세요.");
      }
    }

    userService.updateBirthDate(me.getId(), birthDate);
    me.setBirthDate(birthDate);
  }

  private void changeIsSubscribedToNewsletter() {
    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 이메일 소식 수신 여부: " + me.isSubscribedToNewsletter());

    boolean isSubscribedToNewsletter;
    label:
    while (true) {
      System.out.print("이메일로 소식 받기(y/n): ");
      String yn = sc.nextLine().strip();
      yn = yn.toLowerCase();
      switch (yn) {
        case "x":
          return;
        case "y":
          isSubscribedToNewsletter = true;
          break label;
        case "n":
          isSubscribedToNewsletter = false;
          break label;
        default:
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
    if (friends.isEmpty()) {
      System.out.println("친구 : 없음");
      return;
    }
    String result =
        friends.stream()
            .map(userService::findById)
            .filter(Objects::nonNull)
            .map(User::getEmail)
            .collect(Collectors.joining(", "));
    System.out.println("친구 : " + result);
  }

  private void deleteAccount() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      System.out.println("1. 계정 비활성화");
      System.out.println("2. 계정 삭제");
      String indexStr = sc.nextLine().strip();

      if (indexStr.equals("x")) {
        return;
      }

      switch (indexStr) {
        case "1":
          userService.updateDeactivated(me.getId(), true);
          me.setDeactivated(true);
          logout();
          System.out.println("계정이 비활성화되었습니다. 로그인 시 계정이 활성화됩니다.");
          return;
        case "2":
          userService.deleteById(me.getId());
          me = null;
          System.out.println("계정이 삭제되었습니다.");
          return;
        default:
          System.out.println("1 또는 2를 입력해주세요.");
      }
    }
  }

  private void addFriend() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      showFriends();
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
    }
  }

  private void deleteFriend() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      showFriends();
      System.out.print("삭제할 친구의 이메일: ");
      String email = sc.nextLine().strip();

      if (email.equals("x")) {
        return;
      }

      User friendToDelete = userService.findByEmail(email);
      if (friendToDelete == null) {
        System.out.println("존재하지 않는 친구입니다.");
        continue;
      }
      userService.removeFriend(me.getId(), friendToDelete.getId());
      me.removeFriend(friendToDelete.getId());
    }
  }

  private void showGuilds() {
    System.out.println(guildService.findAll());
  }

  private void createGuild() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      System.out.print("서버 이름: ");
      String name = sc.nextLine();

      if (name.equals("x")) {
        return;
      }

      boolean isPublic;
      label:
      while (true) {
        System.out.print("공개 여부(y/n): ");
        String yn = sc.nextLine();
        yn = yn.toLowerCase();
        switch (yn) {
          case "x":
            return;
          case "y":
            isPublic = true;
            break label;
          case "n":
            isPublic = false;
            break label;
          default:
            System.out.println("y 또는 n을 입력해주세요.\n");
        }
      }

      Guild guild = new Guild(isPublic, me.getId(), name);
      guildService.addMember(guild.getId(), me.getId());

      guildService.addChannel(guild.getId(), new Channel(guild.getId(), "일반", ChannelType.CHAT));
      guildService.addChannel(guild.getId(), new Channel(guild.getId(), "일반", ChannelType.VOICE));

      boolean result = guildService.createGuild(guild);
      if (result) {
        System.out.println(guild.getName() + " 서버가 생성되었습니다.\n");
        return;
      }
      System.out.println("다시 시도해 주세요.\n");
    }
  }

  private void deleteGuild() {
    List<Guild> guilds = guildService.findAll();

    System.out.println("\nx. 뒤로가기");

    if (guilds == null) {
      System.out.println("서버 없음");
      return;
    } else {
      System.out.println("서버 목록: ");
      for (int i = 0; i < guilds.size(); i++) {
        System.out.println(i + 1 + ". " + guilds.get(i));
      }
    }

    while (true) {
      System.out.print("삭제할 서버 번호: ");
      String indexStr = sc.nextLine().strip();

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

      if (index < 1 || index > guilds.size()) {
        System.out.println("유효한 서버 번호를 입력해주세요.\n");
        continue;
      }

      Guild guild = guilds.get(index - 1);

      if (!guild.getOwnerId().equals(me.getId())) {
        System.out.println("삭제할 권한이 없습니다.");
        continue;
      }

      guildService.deleteById(guild.getId());
      System.out.println(guild.getName() + " 서버가 삭제되었습니다.");
      break;
    }
  }

  private void joinGuild() {
    List<Guild> guilds = guildService.findPublicGuilds();

    System.out.println("\nx. 뒤로가기");

    if (guilds == null) {
      System.out.println("서버 없음");
      return;
    } else {
      System.out.println("서버 목록: ");
      for (int i = 0; i < guilds.size(); i++) {
        System.out.println(i + 1 + ". " + guilds.get(i));
      }
    }

    label:
    while (true) {
      System.out.print("들어갈 서버 번호: ");
      String indexStr = sc.nextLine().strip();

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

      if (index < 1 || index > guilds.size()) {
        System.out.println("유효한 서버 번호를 입력해주세요.\n");
        continue;
      }

      Guild guild = guilds.get(index - 1);

      Set<UUID> members = guild.getMembers();

      for (UUID member : members) {
        if (member.equals(me.getId())) {
          System.out.println("이미 들어간 서버입니다.");
          continue label;
        }
      }

      guildService.addMember(guild.getId(), me.getId());
      System.out.println(guild.getName() + " 서버에 입장했습니다.");
      break;
    }
  }

  private void exitGuild() {
    List<Guild> guilds = guildService.findGuildsJoined(me.getId());

    System.out.println("\nx. 뒤로가기");

    if (guilds == null) {
      System.out.println("서버 없음");
      return;
    } else {
      System.out.println("서버 목록: ");
      for (int i = 0; i < guilds.size(); i++) {
        System.out.println(i + 1 + ". " + guilds.get(i));
      }
    }

    while (true) {
      System.out.print("나갈 서버 번호: ");
      String indexStr = sc.nextLine().strip();

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

      if (index < 1 || index > guilds.size()) {
        System.out.println("유효한 서버 번호를 입력해주세요.\n");
        continue;
      }

      Guild guild = guilds.get(index - 1);

      guildService.removeMember(guild.getId(), me.getId());

      System.out.println(guild.getName() + " 서버에서 퇴장했습니다.\n");
      break;
    }
  }

  private void openGuild() {
    List<Guild> guilds = guildService.findGuildsJoined(me.getId());

    System.out.println("\nx. 뒤로가기");

    if (guilds == null) {
      System.out.println("서버 없음");
      return;
    } else {
      System.out.println("서버 목록: ");
      for (int i = 0; i < guilds.size(); i++) {
        System.out.println(i + 1 + ". " + guilds.get(i));
      }
    }

    while (true) {
      System.out.print("열 서버 번호: ");
      String indexStr = sc.nextLine().strip();

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

      if (index < 1 || index > guilds.size()) {
        System.out.println("유효한 서버 번호를 입력해주세요.\n");
        continue;
      }

      Guild guild = guilds.get(index - 1);
      System.out.println(guild.getName() + " 서버를 열었습니다.\n");
      break;
    }
    guildMenu();
  }
}
