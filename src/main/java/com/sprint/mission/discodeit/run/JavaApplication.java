package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import com.sprint.mission.discodeit.enums.friend.FriendRequestStatus;
import com.sprint.mission.discodeit.service.jcf.JcfChannelService;
import com.sprint.mission.discodeit.service.jcf.JcfFriendRequestService;
import com.sprint.mission.discodeit.service.jcf.JcfGuildService;
import com.sprint.mission.discodeit.service.jcf.JcfMessageService;
import com.sprint.mission.discodeit.service.jcf.JcfSurveyService;
import com.sprint.mission.discodeit.service.jcf.JcfUserService;
import com.sprint.mission.discodeit.validation.EmailValidator;
import com.sprint.mission.discodeit.validation.PasswordValidator;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaApplication {
  private final Scanner sc = new Scanner(System.in);
  private final JcfUserService userService = JcfUserService.getInstance();
  private final JcfFriendRequestService friendRequestService =
      JcfFriendRequestService.getInstance();
  private final JcfMessageService messageService = JcfMessageService.getInstance();
  private final JcfChannelService channelService = JcfChannelService.getInstance();
  private final JcfGuildService guildService = JcfGuildService.getInstance();
  private final JcfSurveyService surveyService = JcfSurveyService.getInstance();
  private User me;
  private UUID enteredGuildId;

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

  private void runMenu(String title, List<String> items, List<Runnable> actions, boolean isMain) {
    if (items == null || actions == null || items.size() != actions.size()) {
      System.out.println("메소드 오류");
      return;
    }

    while (true) {
      if (!isMain && me == null) {
        break;
      }

      System.out.println(title);

      for (int i = 0; i < items.size(); i++) {
        System.out.println((i + 1) + ". " + items.get(i));
      }

      System.out.print("메뉴 번호 입력 : ");
      int input = getMenuInput(items.size());

      if (input > 0 && input <= actions.size() && actions.get(input - 1) != null) {
        actions.get(input - 1).run();
      } else {
        System.out.println("올바른 메뉴 번호를 입력해주세요.");
      }

      if (input == items.size()) {
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

    List<String> items = List.of("회원가입", "로그인", "이메일로 회원 조회", "모든 회원 조회", "유저 밴 하기", "종료");
    List<Runnable> actions =
        List.of(
            this::register,
            this::login,
            this::findUserByEmail,
            this::showAllUsers,
            this::banUser,
            this::exitSystem);

    runMenu("=====***** 메인 메뉴 *****=====", items, actions, true);
  }

  private void userMenu() {
    List<String> items = List.of("프로필 편집", "친구 목록 편집", "서버 목록 편집", "다이렉트 메시지 편집", "로그아웃");
    List<Runnable> actions =
        List.of(
            this::editProfileMenu,
            this::editFriendMenu,
            this::editGuildsMenu,
            this::editDirectMessageMenu,
            this::logout);

    runMenu("=====***** 회원 메뉴 *****=====", items, actions, false);
  }

  private void editProfileMenu() {
    List<String> items =
        List.of(
            "이메일 변경",
            "별명 변경",
            "사용자명 변경",
            "비밀번호 변경",
            "생년월일 변경",
            "이메일로 소식 받기 변경",
            "휴대폰 번호 등록/변경",
            "회원 탈퇴",
            "뒤로가기");
    List<Runnable> actions =
        List.of(
            this::changeEmail,
            this::changeNickname,
            this::changeUsername,
            this::changePassword,
            this::changeBirthDate,
            this::changeIsSubscribedToNewsletter,
            this::changePhoneNumber,
            this::deleteAccount,
            this::goPreviousMenu);

    runMenu("=====***** 프로필 편집 메뉴 *****=====", items, actions, false);
  }

  private void editFriendMenu() {
    List<String> items = List.of("친구 요청", "친구 요청 보기", "친구 삭제", "뒤로가기");
    List<Runnable> actions =
        List.of(this::sendFriendRequest, this::viewFriendRequest, this::deleteFriend, () -> {});
    runMenu("=====***** 친구 목록 편집 메뉴 *****=====", items, actions, false);
  }

  private void editGuildsMenu() {
    List<String> items = List.of("모든 서버 조회", "서버 만들기", "서버 삭제", "서버 참가", "서버 나가기", "서버 열기", "뒤로가기");
    List<Runnable> actions =
        List.of(
            this::showGuilds,
            this::createGuild,
            this::deleteGuild,
            this::joinGuild,
            this::exitGuild,
            this::openGuild,
            this::goPreviousMenu);

    runMenu("=====***** 서버 목록 편집 메뉴*****=====", items, actions, false);
  }

  private void guildMenu() {
    Guild guild = guildService.findById(enteredGuildId);

    List<String> items = new ArrayList<>();
    List<Runnable> actions = new ArrayList<>();

    if (guild.getOwnerId().equals(me.getId())) {
      items.add("서버 편집");
      actions.add(this::editGuildMenu);
    }

    items.addAll(List.of("서버 정보 조회", "뒤로가기"));
    actions.addAll(List.of(this::showGuildInfo, this::goPreviousMenu));

    runMenu("=====***** 서버 메뉴 *****=====", items, actions, false);
  }

  // 추후 Permission 클래스로 인가 관리
  private void editGuildMenu() {
    List<String> items =
        List.of(
            "주인 변경", "이름 변경", "공개 여부 변경", "회원 추방", "모든 채널 조회", "채널 생성", "채널 수정", "채널 삭제", "뒤로가기");
    List<Runnable> actions =
        List.of(
            this::changeGuildOwner,
            this::changeGuildName,
            this::changeGuildPublic,
            this::kickMember,
            this::showChannels,
            this::createChannel,
            this::updateChannel,
            this::deleteChannel,
            this::goPreviousMenu);

    runMenu("=====***** 서버 편집 메뉴 *****=====", items, actions, false);
  }

  private void editDirectMessageMenu() {
    List<String> items =
        List.of("다이렉트 메시지 목록 보기", "다이렉트 메시지 보내기", "다이렉트 메시지 보기", "다이렉트 메시지 수정하기", "뒤로가기");
    List<Runnable> actions =
        List.of(
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
            () -> {});

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
      System.err.println(e.getMessage());
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
        System.err.println(e.getMessage());
        continue;
      }

      String password = getInputOrBack("비밀번호 : ");

      try {
        PasswordValidator.validate(password);
      } catch (IllegalArgumentException e) {
        System.err.println(e.getMessage());
        continue;
      }

      try {
        User user = userService.login(email.toLowerCase(), password);
        me = user;
        System.out.println(user.getUsername() + "님, 환영합니다!");
        break;
      } catch (Exception e) {
        System.err.println(e.getMessage());
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
        System.err.println(e.getMessage());
      }
    }
  }

  private void showAllUsers() {
    try {
      List<User> users = userService.findAll();
      System.out.println();
      users.forEach(System.out::println);
    } catch (Exception e) {
      System.err.println(e.getMessage());
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
        System.err.println(e.getMessage());
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
      System.err.println(e.getMessage());
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
      System.err.println(e.getMessage());
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
        System.err.println(e.getMessage());
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
      System.err.println(e.getMessage());
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
      System.err.println(e.getMessage());
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
      System.err.println(e.getMessage());
    }
  }

  private void deleteAccount() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      System.out.println("1. 계정 비활성화");
      System.out.println("2. 계정 삭제");

      String idxStr = getInputOrBack("선택 : ");
      if (idxStr == null) {
        return;
      }

      switch (idxStr) {
        case "1":
          try {
            userService.deactivateAccount(me.getId());
            logout();
            System.out.println("계정이 비활성화되었습니다. 로그인 시 계정이 활성화됩니다.\n");
            return;
          } catch (Exception e) {
            System.err.println(e.getMessage());
            continue;
          }
        case "2":
          try {
            userService.deleteAccount(me.getId());
          } catch (Exception e) {
            System.err.println(e.getMessage());
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

  private void sendFriendRequest() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      String email = getValidEmail("친구 요청할 이메일 : ");
      if (email == null) {
        return;
      }

      User receiver = userService.findByEmail(email);
      if (receiver == null) {
        System.out.println("해당 이메일로 등록된 사용자가 없습니다.");
        continue;
      }

      if (me.equals(receiver)) {
        System.out.println("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        continue;
      }

      try {
        friendRequestService.sendFriendRequest(me.getId(), receiver.getId());
        System.out.println("친구 요청을 보냈습니다.");
        return;
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private void viewFriendRequest() {
    List<FriendRequest> requests = friendRequestService.getReceivedRequests(me.getId()).stream()
        .filter(r -> r.getStatus() == FriendRequestStatus.PENDING)
        .toList();

    if (requests.isEmpty()) {
      System.out.println("\n받은 친구 요청이 없습니다.");
      return;
    }

    for (int i = 0; i < requests.size(); i++) {
      FriendRequest fr = requests.get(i);
      User sender = userService.findById(fr.getSenderId());
      System.out.println((i + 1) + ". " + sender.getUsername());
    }

    System.out.println("\nx. 뒤로가기");
    while (true) {
      String idxStr = getInputOrBack("선택 : ");
      if (idxStr == null) {
        return;
      }

      try {
        int idx = Integer.parseInt(idxStr);
        if (idx < 1 || idx > requests.size()) {
          System.out.println("유효한 번호를 입력해주세요.");
          continue;
        }

        FriendRequest selected = requests.get(idx - 1);
        User sender = userService.findById(selected.getSenderId());

        Boolean accepted = getYesOrNo("친구 요청 수락");

        if (accepted == null) {
          return;
        }

        if (accepted) {
          friendRequestService.acceptFriendRequest(selected.getId());
          System.out.println("친구 요청을 수락했습니다.");
          return;
        }
        friendRequestService.declineFriendRequest(selected.getId());
        System.out.println("친구 요청을 거절했습니다.");
        return;
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private void deleteFriend() {
    if (me.getFriends().isEmpty()) {
      System.out.println("친구 목록이 비어 있습니다.");
      return;
    }

    List<UUID> friends = me.getFriends().stream().toList();

    for (int i = 0; i < friends.size(); i++) {
      User friend = userService.findById(friends.get(i));
      if (friend != null) {
        System.out.printf("%d. %s (%s)\n", i + 1, friend.getUsername(), friend.getEmail());
      }
    }

    while (true) {
      System.out.println("\nx. 뒤로가기");
      String idxStr = getInputOrBack("삭제할 친구 번호: ");
      if (idxStr == null) {
        return;
      }

      try {
        int idx = Integer.parseInt(idxStr) - 1;
        if (idx < 0 || idx >= friends.size()) {
          System.out.println("유효한 번호를 입력해주세요.");
          continue;
        }

        UUID friendId = friends.get(idx);
        userService.removeFriend(me.getId(), friendId);
        System.out.println("친구가 삭제되었습니다.");
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private void showGuilds() {
    List<Guild> guilds = guildService.findAll();
    printGuildList(guilds);
  }

  private void printGuildList(List<Guild> guilds) {
    if (guilds == null || guilds.isEmpty()) {
      System.out.println("서버 없음");
      return;
    }
    System.out.println("서버 목록 : ");
    for (int i = 0; i < guilds.size(); i++) {
      System.out.println((i + 1) + ". " + guilds.get(i));
    }
  }

  private Integer selectGuildIndex(List<Guild> guilds, String prompt) {
    while (true) {
      String idxStr = getInputOrBack(prompt);
      if (idxStr == null) {
        return null;
      }
      try {
        int idx = Integer.parseInt(idxStr);
        if (idx >= 1 && idx <= guilds.size()) {
          return idx - 1;
        }
        System.out.println("유효한 서버 번호를 입력해주세요.\n");
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.\n");
      }
    }
  }

  private void createGuild() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      String name = getInputOrBack("서버 이름 : ");
      if (name == null || name.isBlank()) {
        name = me.getUsername() + "님의 서버";
      }

      Boolean isPublic = getYesOrNo("공개 여부");
      if (isPublic == null) {
        return;
      }

      try {
        Guild guild = guildService.createGuild(new Guild(isPublic, me.getId(), name));
        if (guild != null) {
          guildService.addMember(guild.getId(), me.getId());
          guildService.addChannel(
              guild.getId(), new Channel(guild.getId(), "일반", ChannelType.CHAT));
          guildService.addChannel(
              guild.getId(), new Channel(guild.getId(), "일반", ChannelType.VOICE));
          System.out.println(guild.getName() + " 서버가 생성되었습니다.\n");
          return;
        }
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }

      System.out.println("다시 시도해 주세요.\n");
    }
  }

  private void deleteGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = guildService.findAll();
    printGuildList(guilds);
    if (guilds == null || guilds.isEmpty()) {
      return;
    }

    Integer idx = selectGuildIndex(guilds, "삭제할 서버 번호 : ");
    if (idx == null) {
      return;
    }

    Guild guild = guilds.get(idx);
    if (!guild.getOwnerId().equals(me.getId())) {
      System.out.println("삭제할 권한이 없습니다.");
      return;
    }

    try {
      guildService.deleteById(guild.getId());
      System.out.println(guild.getName() + " 서버가 삭제되었습니다.");
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  private void joinGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = guildService.findPublicGuilds();
    printGuildList(guilds);
    if (guilds == null || guilds.isEmpty()) {
      return;
    }

    Integer idx = selectGuildIndex(guilds, "들어갈 서버 번호 : ");
    if (idx == null) {
      return;
    }

    Guild guild = guilds.get(idx);
    if (guild.getMembers().contains(me.getId())) {
      System.out.println("이미 들어간 서버입니다.");
      return;
    }

    try {
      guildService.addMember(guild.getId(), me.getId());
      System.out.println(guild.getName() + " 서버에 입장했습니다.");
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  private void exitGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = guildService.findGuildsJoined(me.getId());
    printGuildList(guilds);
    if (guilds == null || guilds.isEmpty()) {
      return;
    }

    Integer idx = selectGuildIndex(guilds, "나갈 서버 번호 : ");
    if (idx == null) {
      return;
    }

    Guild guild = guilds.get(idx);
    try {
      guildService.removeMember(guild.getId(), me.getId());
      System.out.println(guild.getName() + " 서버에서 퇴장했습니다.\n");
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  private void openGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = guildService.findGuildsJoined(me.getId());
    printGuildList(guilds);
    if (guilds == null || guilds.isEmpty()) {
      return;
    }

    Integer idx = selectGuildIndex(guilds, "열 서버 번호 : ");
    if (idx == null) {
      return;
    }

    Guild guild = guilds.get(idx);
    System.out.println(guild.getName() + " 서버를 열었습니다.\n");
    enteredGuildId = guild.getId();
    guildMenu();
  }

  private void showGuildInfo() {
    Guild guild = guildService.findById(enteredGuildId);
    System.out.println(guild);
  }

  private Guild checkOwnershipAndReturnGuild() {
    Guild guild = guildService.findById(enteredGuildId);
    if (!guild.getOwnerId().equals(me.getId())) {
      System.out.println("권한이 없습니다.");
      return null;
    }
    return guild;
  }

  private void changeGuildOwner() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    List<UUID> members = new ArrayList<>(guild.getMembers());
    for (int i = 0; i < members.size(); i++) {
      User member = userService.findById(members.get(i));
      System.out.println((i + 1) + ". " + (member != null ? member.getEmail() : members.get(i)));
    }

    while (true) {
      try {
        String memberIdxStr = getInputOrBack("새로운 주인(멤버) 번호 : ");
        if (memberIdxStr == null) {
          return;
        }

        int memberIdx = Integer.parseInt(memberIdxStr);
        if (memberIdx < 1 || memberIdx > members.size()) {
          throw new NumberFormatException("올바른 번호를 입력해주세요.");
        }

        UUID newOwnerId = members.get(memberIdx - 1);
        guildService.updateOwnerId(guild.getId(), newOwnerId);
        if (!newOwnerId.equals(me.getId())) {
          System.out.println("서버 주인이 변경되었습니다 : " + userService.findById(newOwnerId).getUsername());
          break;
        }
        System.out.println("이미 서버 주인입니다.");
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private void changeGuildName() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 이름 : " + guild.getName());

    String guildName = getInputOrBack("변경할 이름 : ");
    if (guildName == null) {
      return;
    }

    try {
      guildService.updateName(guild.getId(), guildName);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  private void changeGuildPublic() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 공개 여부 : " + (guild.isPublic() ? "공개" : "비공개"));

    Boolean isPublic = getYesOrNo("공개 여부");
    if (isPublic != null) {
      guildService.updatePublic(guild.getId(), isPublic);
    }
  }

  private void kickMember() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    Set<UUID> members = guild.getMembers();
    System.out.println("회원 목록:");
    int i = 1;
    List<UUID> memberList = new ArrayList<>(members);
    for (UUID id : memberList) {
      User user = userService.findById(id);
      System.out.println(i + ". " + (user != null ? user.getUsername() : id));
      i++;
    }

    while (true) {
      try {
        String indexStr = getInputOrBack("추방할 멤버 번호 : ");
        if (indexStr == null) {
          return;
        }

        int idx = Integer.parseInt(indexStr) - 1;

        if (idx < 0 || idx >= memberList.size()) {
          throw new NumberFormatException("올바른 번호를 입력해주세요.");
        }
        UUID memberId = memberList.get(idx);
        if (memberId.equals(me.getId())) {
          System.out.println("자기 자신은 추방할 수 없습니다.");
          return;
        }

        guildService.removeMember(guild.getId(), memberId);
        userService.removeGuild(memberId, guild.getId());
        String username = userService.findById(memberId).getUsername();
        System.out.println(username + " 멤버가 추방되었습니다.");
        break;
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private void showChannels() {
    Guild guild = guildService.findById(enteredGuildId);
    List<Channel> channels = guild.getChannels();
    if (channels.isEmpty()) {
      System.out.println("채널 없음");
      return;
    }
    int i = 1;
    for (Channel ch : channels) {
      System.out.println(i++ + ". " + ch);
    }
  }

  private void createChannel() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    while (true) {
      try {
        String name = getInputOrBack("채널 이름 : ");
        if (name == null) {
          return;
        }

        System.out.println("채널 유형 : ");
        for (int i = 0; i < ChannelType.values().length; i++) {
          System.out.println((i + 1) + ". " + ChannelType.values()[i]);
        }

        int typeIdx = getMenuInput(ChannelType.values().length);

        if (typeIdx < 1 || typeIdx > ChannelType.values().length) {
          throw new NumberFormatException();
        }

        ChannelType type = ChannelType.values()[typeIdx - 1];

        Channel newChannel = new Channel(guild.getId(), name, type);
        guildService.addChannel(guild.getId(), newChannel);
        System.out.println("채널이 생성되었습니다.");
        break;
      } catch (NumberFormatException e) {
        System.out.println("올바른 번호를 입력해주세요");
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private void updateChannel() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    List<Channel> channels = guild.getChannels();
    if (channels == null || channels.isEmpty()) {
      System.out.println("채널 없음");
      return;
    }

    System.out.println("\nx. 뒤로가기");
    showChannels();
    while (true) {
      String idxStr = getInputOrBack("수정할 채널 번호 : ");
      if (idxStr == null) {
        return;
      }

      try {
        int idx = Integer.parseInt(idxStr) - 1;
        if (idx < 0 || idx >= channels.size()) {
          throw new NumberFormatException();
        }

        Channel channel = channels.get(idx);

        System.out.println("현재 채널 이름 : " + channel.getName());
        final String newName = getInputOrBack("새 채널 이름 : ");

        System.out.println("현재 채널 유형 : " + channel.getType());
        System.out.print("새 채널 유형 : ");
        System.out.println("1. 채팅");
        System.out.println("2. 음성");
        System.out.println("3. 포럼");
        String typeIdxStr = getInputOrBack("새 채널 번호 : ");
        if (typeIdxStr == null) {
          return;
        }

        int typeIdx = Integer.parseInt(typeIdxStr);
        if (typeIdx < 1 || typeIdx > 3) {
          throw new NumberFormatException();
        }

        ChannelType type = ChannelType.values()[typeIdx - 1];

        if (newName != null && !newName.isBlank()) {
          channel.setName(newName);
        }
        //
        System.out.println("채널이 수정되었습니다.");
      } catch (NumberFormatException e) {
        System.out.println("올바른 번호를 입력해주세요");
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private void deleteChannel() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    List<Channel> channels = guild.getChannels();
    if (channels == null || channels.isEmpty()) {
      System.out.println("채널 없음");
      return;
    }

    System.out.println("\nx. 뒤로가기");
    showChannels();
    while (true) {
      String idxStr = getInputOrBack("삭제할 채널 번호 : ");
      if (idxStr == null) {
        return;
      }

      try {
        int idx = Integer.parseInt(idxStr) - 1;
        if (idx < 0 || idx >= channels.size()) {
          throw new NumberFormatException();
        }

        Channel channel = channels.get(idx);

        guildService.removeChannel(guild.getId(), channel);
        System.out.println("채널이 삭제되었습니다.");
        break;
      } catch (NumberFormatException e) {
        System.out.println("올바른 번호를 입력해주세요.");
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }
}
