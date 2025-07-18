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
import com.sprint.mission.discodeit.validation.EmailValidator;
import com.sprint.mission.discodeit.validation.PasswordValidator;
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

  private int getMenuInput(int maxNum) {
    while (true) {
      try {
        int menuNum = Integer.parseInt(sc.nextLine());
        if (menuNum >= 1 && menuNum <= maxNum) {
          return menuNum;
        }
        System.out.println("올바른 메뉴 번호를 입력해주세요.");
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
    }
  }

  private String getInputOrBack(String prompt) {
    System.out.print(prompt);
    String input = sc.nextLine().strip();
    return input.equalsIgnoreCase("x") ? null : input;
  }

  private Boolean getYesOrNo(String prompt) {
    while (true) {
      String answer = getInputOrBack(prompt + "(y/n) : ");
      if (answer == null) {
        return null;
      }
      switch (answer.strip().toLowerCase()) {
        case "y":
          return true;
        case "n":
          return false;
        default:
          System.out.println("y 또는 n을 입력해주세요.\n");
      }
    }
  }

  private String getValidEmail(String prompt) {
    String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    while (true) {
      String email = getInputOrBack(prompt);
      if (email == null) {
        return null;
      }
      if (email.matches(emailPattern)) {
        return email.toLowerCase();
      }
      System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
    }
  }

  private String getValidPassword(String prompt) {
    String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$";
    while (true) {
      String password = getInputOrBack(prompt);
      if (password == null) {
        return null;
      }
      if (password.matches(passwordPattern)) {
        return password;
      }
      System.out.println("비밀번호는 영문, 숫자, 특수 문자 조합 8자 이상을 입력해 주세요.");
    }
  }

  private LocalDate getValidDate(String message) {
    while (true) {
      try {
        System.out.println(message);
        String yearStr = getInputOrBack("년 : ");
        if (yearStr == null) {
          return null;
        }
        String monthStr = getInputOrBack("월 : ");
        if (monthStr == null) {
          return null;
        }
        String dayStr = getInputOrBack("일 : ");
        if (dayStr == null) {
          return null;
        }
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);
        return LocalDate.of(year, month, day);
      } catch (NumberFormatException e) {
        System.out.println("숫자 형식이 올바르지 않습니다. 다시 입력해주세요.");
      } catch (DateTimeException e) {
        System.out.println("유효하지 않은 날짜입니다. 다시 입력해주세요.");
      }
    }
  }

  private void runMenu(String title, String[] items, Runnable[] actions, boolean isMain) {
    if (items == null || actions == null || items.length != actions.length) {
      System.out.println("메소드 오류");
      return;
    }

    while (true) {
      if (!isMain && me == null) {
        break;
      }

      System.out.println(title);

      for (int i = 0; i < items.length; i++) {
        System.out.println((i + 1) + ". " + items[i]);
      }

      System.out.print("메뉴 번호 입력 : ");
      int input = getMenuInput(items.length);

      if (input > 0 && input <= actions.length && actions[input - 1] != null) {
        actions[input - 1].run();
      } else {
        System.out.println("올바른 메뉴 번호를 입력해주세요.");
      }

      if (input == items.length) {
        break;
      }
    }
  }

  private void exitSystem() {
    System.out.println("\n프로그램 종료");
    sc.close();
    System.exit(0);
  }

  private void goPreviousMenu() {
    System.out.println("\n*뒤로가기*");
  }

  private void mainMenu() {
    userService.registerUser(
        new User("a@a.aa", "1", "1111aaaa", LocalDate.of(1995, 4, 10), true, "1")); // 테스트용
    userService.registerUser(
        new User("b@b.bb", "2", "2222bbbb", LocalDate.of(1995, 4, 11), false, "2")); // 테스트용
    System.out.println("\n========== Discodeit ==========");

    String[] items = {"회원가입", "로그인", "이메일로 회원 조회", "모든 회원 조회", "유저 밴 하기", "종료"};
    Runnable[] actions = {
      this::register,
      this::login,
      this::findUserByEmail,
      this::showAllUsers,
      this::banUser,
      this::exitSystem,
    };
    runMenu("=====***** 메인 메뉴 *****=====", items, actions, true);
  }

  private void userMenu() {
    String[] items = {"프로필 편집", "친구 목록 편집", "서버 목록 편집", "다이렉트 메시지 편집", "로그아웃"};
    Runnable[] actions = {
      this::editProfileMenu,
      this::editFriendMenu,
      this::editGuildMenu,
      this::editDirectMessageMenu,
      this::logout,
    };
    runMenu("=====***** 회원 메뉴 *****=====", items, actions, false);
  }

  private void editProfileMenu() {
    String[] items = {
      "이메일 변경",
      "별명 변경",
      "사용자명 변경",
      "비밀번호 변경",
      "생년월일 변경",
      "이메일로 소식 받기 변경",
      "휴대폰 번호 등록/변경",
      "회원 탈퇴",
      "뒤로가기"
    };
    Runnable[] actions = {
      this::changeEmail,
      this::changeNickname,
      this::changeUsername,
      this::changePassword,
      this::changeBirthDate,
      this::changeIsSubscribedToNewsletter,
      this::changePhoneNumber,
      this::deleteAccount,
      this::goPreviousMenu,
    };
    runMenu("=====***** 프로필 편집 메뉴 *****=====", items, actions, false);
  }

  private void editFriendMenu() {
    String[] items = {"친구 추가", "친구 삭제", "뒤로가기"};
    Runnable[] actions = {this::addFriend, this::deleteFriend, () -> {}};
    runMenu("=====***** 친구 목록 편집 메뉴 *****=====", items, actions, false);
  }

  private void editGuildMenu() {
    String[] items = {"모든 서버 조회", "서버 만들기", "서버 삭제", "서버 참가", "서버 나가기", "서버 열기", "뒤로가기"};
    Runnable[] actions = {
      this::showGuilds,
      this::createGuild,
      this::deleteGuild,
      this::joinGuild,
      this::exitGuild,
      this::openGuild,
      this::goPreviousMenu,
    };
    runMenu("=====***** 서버 목록 편집 메뉴*****=====", items, actions, false);
  }

  private void guildMenu() {
    String[] items = {
      "서버 정보 조회", "서버 주인 변경", "공개 여부 변경", "회원 추방", "채널들 조회", "채널 생성", "채널 수정", "채널 삭제", "뒤로가기"
    };
    Runnable[] actions = {
      () -> {
        /* 서버 정보 조회 */
      },
      () -> {
        /* 서버 주인 변경 */
      },
      () -> {
        /* 공개 여부 변경 */
      },
      () -> {
        /* 회원 추방 */
      },
      () -> {
        /* 채널들 조회 */
      },
      () -> {
        /* 채널 생성 */
      },
      () -> {
        /* 채널 수정 */
      },
      () -> {
        /* 채널 삭제 */
      },
      () -> {}
    };
    runMenu("=====***** 서버 메뉴 *****=====", items, actions, false);
  }

  private void editDirectMessageMenu() {
    String[] items = {"다이렉트 메시지 목록 보기", "다이렉트 메시지 보내기", "다이렉트 메시지 보기", "다이렉트 메시지 수정하기", "뒤로가기"};
    Runnable[] actions = {
      () -> {
        /* 다이렉트 메시지 목록 보기 */
      },
      () -> {
        /* 다이렉트 메시지 보내기 */
      },
      () -> {
        /* 다이렉트 메시지 보기 */
      },
      () -> {
        /* 다이렉트 메시지 수정하기 */
      },
      () -> {}
    };
    runMenu("=====***** 다이렉트 메시지 편집 메뉴*****=====", items, actions, false);
  }

  private void register() {
    System.out.println("\nx. 뒤로가기");

    String email;
    while (true) {
      email = getValidEmail("이메일 : ");
      if (email == null) {
        return;
      }

      if (userService.findByEmail(email) == null) {
        break;
      }

      System.out.println("중복된 이메일입니다. 다시 입력해주세요.\n");
    }

    String nickname = getInputOrBack("별명(선택) : ");
    if (nickname == null) {
      return;
    }

    String username;
    while (true) {
      username = getInputOrBack("사용자명 : ");
      if (username == null) {
        return;
      }
      if (!username.isEmpty()) {
        break;
      }
      System.out.println("사용자명은 필수입니다.");
    }

    String password = getValidPassword("비밀번호 : ");
    if (password == null) {
      return;
    }

    LocalDate birthDate = getValidDate("생년월일");
    if (birthDate == null) {
      return;
    }

    Boolean isSubscribedToNewsletter = getYesOrNo("이메일로 소식 받기");
    if (isSubscribedToNewsletter == null) {
      return;
    }

    try {
      User user =
          userService.registerUser(
              new User(
                  email.toLowerCase(),
                  username,
                  password,
                  birthDate,
                  isSubscribedToNewsletter,
                  nickname));
      if (user != null) {
        System.out.println("성공적으로 회원가입을 완료하였습니다.\n");
      } else {
        System.out.println("다시 시도해 주세요.\n");
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void login() {
    System.out.println("\nx. 뒤로가기");

    while (true) {
      String email = getValidEmail("이메일 : ");
      if (email == null) {
        return;
      }

      try {
        EmailValidator.validate(email);
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        continue;
      }

      String password = getInputOrBack("비밀번호 : ");

      try {
        PasswordValidator.validate(password);
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        continue;
      }

      try {
        User user = userService.login(email.toLowerCase(), password);
        me = user;
        System.out.println(user.getUsername() + "님, 환영합니다!");
        break;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
    userMenu();
  }

  private void logout() {
    if (me != null) {
      userService.logout(me.getId());
    }
    me = null;
    System.out.println("로그아웃");
  }

  private void findUserByEmail() {
    while (true) {
      System.out.println("\nx. 뒤로가기");
      String email = getValidEmail("이메일 : ");
      if (email == null) {
        return;
      }

      User user = userService.findByEmail(email.toLowerCase());

      if (user == null) {
        System.out.println("등록된 회원이 없습니다.");
      } else {
        System.out.println(user);
      }
    }
  }

  private void banUser() {
    while (true) {
      showAllUsers();
      System.out.println("x. 뒤로가기");
      String email = getInputOrBack("정지시킬 유저의 이메일 : ");
      if (email == null) {
        return;
      }

      User user = userService.findByEmail(email.toLowerCase());

      if (user == null) {
        System.out.println("등록된 회원이 없습니다.\n");
        continue;
      }

      try {
        userService.updateBanned(user.getId(), true);
        System.out.println("계정 정지 : " + user.getEmail());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void showAllUsers() {
    try {
      List<User> users = userService.findAll();
      System.out.println();
      users.forEach(System.out::println);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void changeEmail() {
    String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 이메일 : " + me.getEmail());

    while (true) {
      String email = getInputOrBack("변경할 이메일 : ");
      if (email == null) {
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

      User duplicatedUser = userService.findByEmail(email.toLowerCase());
      if (duplicatedUser != null) {
        System.out.println("중복된 이메일입니다. 다시 입력해주세요.");
        continue;
      }

      try {
        userService.updateEmail(me.getId(), email);
        me.setEmail(email);
        break;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void changeNickname() {
    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 별명 : " + me.getNickname());
    String nickname = getInputOrBack("변경할 별명 : ");
    if (nickname == null) {
      return;
    }

    try {
      userService.updateNickname(me.getId(), nickname);
      me.setNickname(nickname);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void changeUsername() {
    System.out.println("\nx. 뒤로가기");
    String oldUsername = me.getUsername();
    String newUsername;

    while (true) {
      System.out.println("현재 사용자명 : " + oldUsername);
      newUsername = getInputOrBack("변경할 사용자명 : ");
      if (newUsername == null) {
        return;
      }

      if (!newUsername.isEmpty()) {
        break;
      }

      System.out.println("사용자명은 필수입니다.\n");
    }

    try {
      userService.updateUsername(me.getId(), newUsername);
      me.setUsername(newUsername);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void changePassword() {
    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 비밀번호 : " + me.getPassword());

    while (true) {
      String password = getValidPassword("변경할 비밀번호 : ");
      if (password == null) {
        return;
      }

      if (password.equals(me.getPassword())) {
        System.out.println("같은 비밀번호입니다.");
        continue;
      }

      try {
        userService.updatePassword(me.getId(), password);
        me.setPassword(password);
        break;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void changeBirthDate() {
    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 생년월일 : " + me.getBirthDate());
    LocalDate birthDate;

    birthDate = getValidDate("변경할 생년월일");
    if (birthDate == null) {
      return;
    }

    try {
      userService.updateBirthDate(me.getId(), birthDate);
      me.setBirthDate(birthDate);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void changeIsSubscribedToNewsletter() {
    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 이메일 소식 수신 여부 : " + (me.isSubscribedToNewsletter() ? "yes" : "no"));

    Boolean isSubscribedToNewsletter = getYesOrNo("이메일로 소식 받기");
    if (isSubscribedToNewsletter == null) {
      return;
    }

    try {
      userService.updateSubscribedToNewsletter(me.getId(), isSubscribedToNewsletter);
      me.setSubscribedToNewsletter(isSubscribedToNewsletter);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void changePhoneNumber() {
    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 휴대폰 번호 : " + me.getPhoneNumber());
    String phoneNumber = getInputOrBack("변경할 휴대폰 번호 : ");
    if (phoneNumber == null) {
      return;
    }

    try {
      userService.updatePhoneNumber(me.getId(), phoneNumber);
      me.setPhoneNumber(phoneNumber);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void deleteAccount() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      System.out.println("1. 계정 비활성화");
      System.out.println("2. 계정 삭제");
      String indexStr = getInputOrBack("선택 : ");
      if (indexStr == null) {
        return;
      }

      switch (indexStr) {
        case "1":
          try {
            userService.updateDeactivated(me.getId(), true);
            me.setDeactivated(true);
            logout();
            System.out.println("계정이 비활성화되었습니다. 로그인 시 계정이 활성화됩니다.\n");
            return;
          } catch (Exception e) {
            System.out.println(e.getMessage());
            continue;
          }
        case "2":
          try {
            userService.deleteById(me.getId());
          } catch (Exception e) {
            System.out.println(e.getMessage());
            continue;
          }
          me = null;
          System.out.println("계정이 삭제되었습니다.\n");
          return;
        default:
          System.out.println("1 또는 2를 입력해주세요.");
      }
    }
  }

  private void showFriends() {
    Set<UUID> friends = me.getFriends();
    if (friends.isEmpty()) {
      System.out.println("친구 : 없음");
      return;
    }

    String friendsStr =
        friends.stream()
            .map(userService::findById)
            .filter(Objects::nonNull)
            .map(User::getEmail)
            .collect(Collectors.joining(", "));
    System.out.println("친구 : " + friendsStr);
  }

  private void addFriend() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      showFriends();

      String email = getValidEmail("추가할 친구의 이메일 : ");
      if (email == null) {
        return;
      }

      User friend = userService.findByEmail(email.toLowerCase());
      if (friend == null) {
        System.out.println("존재하지 않는 이메일입니다.\n");
        continue;
      }

      if (me.equals(friend)) {
        System.out.println("뭐야 나잖아");
        continue;
      }

      try {
        userService.addFriend(me.getId(), friend.getId());
        me.addFriend(friend.getId());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void deleteFriend() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      showFriends();

      String email = getValidEmail("삭제할 친구의 이메일 : ");
      if (email == null) {
        return;
      }

      User friendToDelete = userService.findByEmail(email.toLowerCase());
      if (friendToDelete == null) {
        System.out.println("존재하지 않는 이메일입니다.");
        continue;
      }
      try {
        userService.removeFriend(me.getId(), friendToDelete.getId());
        me.removeFriend(friendToDelete.getId());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void showGuilds() {
    System.out.println(guildService.findAll());
  }

  private void createGuild() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      String name = getInputOrBack("서버 이름 : ");

      Boolean isPublic = getYesOrNo("공개 여부");
      if (isPublic == null) {
        return;
      }

      try {
        Guild guild = new Guild(isPublic, me.getId(), name);
        boolean result = guildService.createGuild(guild);
        if (result) {
          guildService.addMember(guild.getId(), me.getId());
          guildService.addChannel(
              guild.getId(), new Channel(guild.getId(), "일반", ChannelType.CHAT));
          guildService.addChannel(
              guild.getId(), new Channel(guild.getId(), "일반", ChannelType.VOICE));
          System.out.println(guild.getName() + " 서버가 생성되었습니다.\n");
          return;
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }

      System.out.println("다시 시도해 주세요.\n");
    }
  }

  private void deleteGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = guildService.findAll();

    if (guilds == null) {
      System.out.println("서버 없음");
      return;
    } else {
      System.out.println("서버 목록 : ");
      for (int i = 0; i < guilds.size(); i++) {
        System.out.println(i + 1 + ". " + guilds.get(i));
      }
    }

    while (true) {
      String indexStr = getInputOrBack("삭제할 서버 번호 : ");
      if (indexStr == null) {
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

      try {
        guildService.deleteById(guild.getId());
        System.out.println(guild.getName() + " 서버가 삭제되었습니다.");
        break;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void joinGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = guildService.findPublicGuilds();

    if (guilds == null) {
      System.out.println("서버 없음");
      return;
    } else {
      System.out.println("서버 목록 : ");
      for (int i = 0; i < guilds.size(); i++) {
        System.out.println(i + 1 + ". " + guilds.get(i));
      }
    }

    label:
    while (true) {
      String indexStr = getInputOrBack("들어갈 서버 번호 : ");
      if (indexStr == null) {
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

      try {
        guildService.addMember(guild.getId(), me.getId());
        System.out.println(guild.getName() + " 서버에 입장했습니다.");
        break;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void exitGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = guildService.findGuildsJoined(me.getId());

    if (guilds == null) {
      System.out.println("서버 없음");
      return;
    } else {
      System.out.println("서버 목록 : ");
      for (int i = 0; i < guilds.size(); i++) {
        System.out.println(i + 1 + ". " + guilds.get(i));
      }
    }

    while (true) {
      String indexStr = getInputOrBack("나갈 서버 번호 : ");
      if (indexStr == null) {
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

      try {
        guildService.removeMember(guild.getId(), me.getId());
        System.out.println(guild.getName() + " 서버에서 퇴장했습니다.\n");
        break;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void openGuild() {
    List<Guild> guilds = guildService.findGuildsJoined(me.getId());

    System.out.println("\nx. 뒤로가기");

    if (guilds == null) {
      System.out.println("서버 없음");
      return;
    } else {
      System.out.println("서버 목록 : ");
      for (int i = 0; i < guilds.size(); i++) {
        System.out.println(i + 1 + ". " + guilds.get(i));
      }
    }

    while (true) {
      String indexStr = getInputOrBack("열 서버 번호 : ");
      if (indexStr == null) {
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
