package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import com.sprint.mission.discodeit.service.jcf.JcfChannelService;
import com.sprint.mission.discodeit.service.jcf.JcfFriendRequestService;
import com.sprint.mission.discodeit.service.jcf.JcfGuildService;
import com.sprint.mission.discodeit.service.jcf.JcfMessageService;
import com.sprint.mission.discodeit.service.jcf.JcfSurveyService;
import com.sprint.mission.discodeit.service.jcf.JcfUserService;
import com.sprint.mission.discodeit.utility.InputHandler;
import com.sprint.mission.discodeit.validation.PasswordValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaApplication {
  private final JcfUserService userService = JcfUserService.getInstance();
  private final JcfFriendRequestService friendRequestService =
      JcfFriendRequestService.getInstance();
  private final JcfMessageService messageService = JcfMessageService.getInstance();
  private final JcfGuildService guildService = JcfGuildService.getInstance();
  private final JcfChannelService channelService = JcfChannelService.getInstance();
  private final JcfSurveyService surveyService = JcfSurveyService.getInstance();
  private User me;
  private UUID enteredGuildId;

  public static void main(String[] args) {
    new JavaApplication().mainMenu();
  }

  private void seedTestUsers() {
    try {
      userService.register(
          new User("a@a.aa", "user1", "1111aaaa", LocalDate.of(1995, 4, 10), true, "globalName1"));
      userService.register(
          new User("b@b.bb", "user2", "2222bbbb", LocalDate.of(1995, 4, 11), false, "globalName2"));
      userService.register(
          new User("c@c.cc", "user3", "3333cccc", LocalDate.of(1995, 3, 11), false, "globalName3"));
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private static class Menu {
    private final String title;
    private final List<String> items = new ArrayList<>();
    private final List<Runnable> actions = new ArrayList<>();
    private final boolean requiresLogin;

    public Menu(String title, boolean requiresLogin) {
      this.title = title != null ? title : "메뉴";
      this.requiresLogin = requiresLogin;
    }

    public Menu(String title) {
      this(title, true);
    }

    public Menu add(String label, Runnable action) {
      if (label != null && action != null) {
        items.add(label);
        actions.add(action);
      }
      return this;
    }

    public String getTitle() {
      return title;
    }

    public List<String> getItems() {
      return Collections.unmodifiableList(items);
    }

    public List<Runnable> getActions() {
      return Collections.unmodifiableList(actions);
    }

    public boolean isValid() {
      return items.size() == actions.size() && !items.isEmpty();
    }
  }

  private void runMenu(Menu menu) {
    if (!menu.isValid()) {
      System.out.println("⚠ 메뉴 구성이 잘못되었습니다.");
      return;
    }

    while (!menu.requiresLogin || me != null) {
      System.out.println("\n" + menu.getTitle());

      for (int i = 0; i < menu.getItems().size(); i++) {
        System.out.println((i + 1) + ". " + menu.getItems().get(i));
      }

      int input = InputHandler.getMenuInput(menu.getItems().size(), "메뉴 번호 입력 : ");

      Runnable action = menu.getActions().get(input - 1);
      if (action != null) {
        action.run();
      } else {
        System.out.println("올바른 메뉴 번호를 입력해주세요.");
      }

      if (input == menu.getItems().size()) {
        break;
      }
    }
  }

  private void exitSystem() {
    System.out.println("\n프로그램 종료");
    InputHandler.close();
    System.exit(0);
  }

  private void goPreviousMenu() {
    System.out.println("\n*뒤로가기*");
  }

  private void mainMenu() {
    seedTestUsers();

    runMenu(
        new Menu("=====***** 메인 메뉴 *****=====", false)
            .add("회원가입", this::register)
            .add("로그인", this::login)
            .add("이메일로 회원 조회", this::findUserByEmail)
            .add("모든 회원 조회", this::showAllUsers)
            .add("유저 검색", this::searchUsers)
            .add("유저 밴", this::banUser)
            .add("유저 밴 해제", this::unbanUser)
            .add("종료", this::exitSystem));
  }

  private void userMenu() {
    runMenu(
        new Menu("=====***** 회원 메뉴 *****=====")
            .add("프로필 편집", this::editProfileMenu)
            .add("친구 목록 편집", this::editFriendMenu)
            .add("서버 목록 편집", this::editGuildsMenu)
            .add("다이렉트 메시지 편집", this::editDirectMessageMenu)
            .add("로그아웃", this::logout));
  }

  private void editProfileMenu() {
    runMenu(
        new Menu("=====***** 프로필 편집 메뉴 *****=====")
            .add("이메일 변경", this::changeEmail)
            .add("별명 변경", this::changeGlobalName)
            .add("사용자명 변경", this::changeUsername)
            .add("비밀번호 변경", this::changePassword)
            .add("생년월일 변경", this::changeBirthDate)
            .add("이메일로 소식 받기 변경", this::changeIsSubscribedToNewsletter)
            .add("휴대폰 번호 등록/변경", this::changePhoneNumber)
            .add("회원 탈퇴", this::deleteAccount)
            .add("뒤로가기", this::goPreviousMenu));
  }

  private void editFriendMenu() {
    runMenu(
        new Menu("=====***** 친구 목록 편집 메뉴 *****=====")
            .add("친구 목록 보기", this::showFriends)
            .add("친구 요청", this::sendFriendRequest)
            .add("친구 요청 취소", this::viewSentFriendRequests)
            .add("친구 요청 응답", this::viewReceivedFriendRequests)
            .add("친구 삭제", this::deleteFriend)
            .add("뒤로가기", this::goPreviousMenu));
  }

  private void editGuildsMenu() {
    runMenu(
        new Menu("=====***** 서버 목록 편집 메뉴*****=====")
            .add("모든 서버 조회", this::showGuilds)
            .add("서버 만들기", this::createGuild)
            .add("서버 삭제", this::deleteGuild)
            .add("서버 참가", this::joinGuild)
            .add("서버 나가기", this::exitGuild)
            .add("서버 열기", this::openGuild)
            .add("뒤로가기", this::goPreviousMenu));
  }

  private void guildMenu() {
    Guild guild = guildService.findById(enteredGuildId);
    Menu menu = new Menu("=====***** 서버 메뉴 *****=====");

    if (guild.getOwnerId().equals(me.getId())) {
      menu.add("서버 편집", this::editGuildMenu);
    }

    menu.add("서버 정보 조회", this::showGuildInfo).add("뒤로가기", this::goPreviousMenu);

    runMenu(menu);
  }

  private void editGuildMenu() {
    runMenu(
        new Menu("=====***** 서버 편집 메뉴 *****=====")
            .add("주인 변경", this::changeGuildOwner)
            .add("이름 변경", this::changeGuildName)
            .add("공개 여부 변경", this::changeGuildPublic)
            .add("회원 추방", this::kickMember)
            .add("모든 채널 조회", this::showChannels)
            .add("채널 생성", this::createChannel)
            .add("채널 수정", this::updateChannel)
            .add("채널 삭제", this::deleteChannel)
            .add("뒤로가기", this::goPreviousMenu));
  }

  private void editDirectMessageMenu() {
    runMenu(
        new Menu("=====***** 다이렉트 메시지 편집 메뉴*****=====")
            .add("다이렉트 메시지 목록 보기", () -> {})
            .add("다이렉트 메시지 보내기", () -> {})
            .add("다이렉트 메시지 보기", () -> {})
            .add("다이렉트 메시지 수정하기", () -> {})
            .add("뒤로가기", this::goPreviousMenu));
  }

  private void register() {
    System.out.println("\nx. 뒤로가기");

    String email;
    while (true) {
      email = InputHandler.getValidEmail("이메일 : ");
      if (email == null) {
        return;
      }

      if (userService.findByEmail(email) == null) {
        break;
      }

      System.out.println("⚠ 중복된 이메일입니다. 다시 입력해주세요.");
    }

    String globalName = InputHandler.getInputOrBack("별명(선택) : ");
    if (globalName == null) {
      return;
    }

    String username;
    while (true) {
      username = InputHandler.getInputOrBack("사용자명 : ");
      if (username == null) {
        return;
      }

      if (!username.isEmpty()) {
        break;
      }

      System.out.println("⚠ 사용자명은 필수입니다.");
    }

    String password = InputHandler.getValidPassword("비밀번호 : ");
    if (password == null) {
      return;
    }

    LocalDate birthDate = InputHandler.getValidDate("생년월일");
    if (birthDate == null) {
      return;
    }

    Boolean isSubscribedToNewsletter = InputHandler.getYesOrNo("이메일로 소식 받기");
    if (isSubscribedToNewsletter == null) {
      return;
    }

    if (globalName.isBlank()) {
      globalName = username;
    }

    try {
      User user =
          userService.register(
              new User(
                  email.toLowerCase(),
                  username,
                  password,
                  birthDate,
                  isSubscribedToNewsletter,
                  globalName));
      if (user != null) {
        System.out.println("✅ 회원가입이 완료되었습니다.");
      } else {
        System.out.println("🚫 다시 시도해주세요.\n");
      }
    } catch (Exception e) {
      System.out.println("🚫 " + e.getMessage());
    }
  }

  private void login() {
    System.out.println("\nx. 뒤로가기");

    while (true) {
      String email = InputHandler.getValidEmail("이메일 : ");
      if (email == null) {
        return;
      }

      String password = InputHandler.getValidPassword("비밀번호 : ");
      if (password == null) {
        return;
      }

      try {
        PasswordValidator.validate(password);
        User user = userService.login(email, password);
        me = user;
        System.out.println("\n" + user.getUsername() + "님, 환영합니다!");
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
      System.out.println(me.getUsername() + "님, 로그아웃 되었습니다.");
      me = null;
    } else {
      System.out.println("현재 로그인된 사용자가 없습니다.");
    }
  }

  private void findUserByEmail() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      String email = InputHandler.getValidEmail("이메일 : ");
      if (email == null) {
        return;
      }

      User user = userService.findByEmail(email);
      System.out.println(user != null ? user : "등록된 회원이 없습니다.");
    }
  }

  private void searchUsers() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      String keyword = InputHandler.getInputOrBack("검색할 닉네임, 이메일 또는 사용자명 입력 : ");
      if (keyword == null || keyword.isBlank()) {
        return;
      }

      List<User> results = userService.searchUsers(keyword);
      if (results.isEmpty()) {
        System.out.println("🔍 해당 조건에 맞는 사용자가 없습니다.");
        continue;
      }

      System.out.println("\n검색 결과:");
      results.forEach(
          user ->
              System.out.printf(
                  "- 닉네임: %s | 이메일: %s | 사용자명: %s%n",
                  user.getGlobalName(), user.getEmail(), user.getUsername()));
      break;
    }
  }

  private void banUser() {
    while (true) {
      List<User> activeUsers = userService.findAll().stream().filter(u -> !u.isBanned()).toList();

      if (activeUsers.isEmpty()) {
        System.out.println("\n정지되지 않은 유저가 없습니다.");
        return;
      }

      System.out.println("\n정지되지 않은 유저 목록:");
      activeUsers.forEach(u -> System.out.println("- " + u.getEmail()));

      System.out.println("\nx. 뒤로가기");

      String email = InputHandler.getValidEmail("정지시킬 유저의 이메일 : ");
      if (email == null) {
        return;
      }

      User user = userService.findByEmail(email);
      if (user == null) {
        System.out.println("\n해당 이메일의 정지되지 않은 유저를 찾을 수 없습니다.");
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

  private void unbanUser() {
    while (true) {
      List<User> activeUsers = userService.findAll().stream().filter(User::isBanned).toList();

      if (activeUsers.isEmpty()) {
        System.out.println("정지된 유저가 없습니다.");
        return;
      }

      System.out.println("\n정지된 유저 목록:");
      activeUsers.forEach(u -> System.out.println("- " + u.getEmail()));

      System.out.println("\nx. 뒤로가기");

      String email = InputHandler.getInputOrBack("정지 해제할 유저의 이메일 : ");
      if (email == null) {
        return;
      }

      User user = userService.findByEmail(email.toLowerCase());

      if (user == null) {
        System.out.println("\n입력된 이메일의 정지된 유저를 찾을 수 없습니다.");
        continue;
      }

      try {
        userService.updateBanned(user.getId(), false);
        System.out.println("계정 정지 해제 : " + user.getEmail());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void showAllUsers() {
    try {
      System.out.println();
      userService.findAll().forEach(System.out::println);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void changeEmail() {
    String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 이메일 : " + me.getEmail());

    while (true) {
      String email = InputHandler.getValidEmail("변경할 이메일 : ");
      if (email == null) {
        return;
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

      try {
        userService.updateEmail(me.getId(), email);
        me.setEmail(email);
        System.out.println("✅ 이메일이 성공적으로 변경되었습니다: " + email);
        break;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void changeGlobalName() {
    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 별명 : " + me.getGlobalName());
    String globalName = InputHandler.getInputOrBack("변경할 별명 : ");
    if (globalName == null) {
      return;
    }

    try {
      userService.updateGlobalName(me.getId(), globalName);
      me.setGlobalName(globalName);
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
      newUsername = InputHandler.getInputOrBack("변경할 사용자명 : ");
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
      String password = InputHandler.getValidPassword("변경할 비밀번호 : ");
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

    birthDate = InputHandler.getValidDate("변경할 생년월일");
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

    Boolean isSubscribedToNewsletter = InputHandler.getYesOrNo("이메일로 소식 받기");
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
    String phoneNumber = InputHandler.getInputOrBack("변경할 휴대폰 번호 : ");
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

      String idxStr = InputHandler.getInputOrBack("선택 : ");
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
            System.out.println(e.getMessage());
            continue;
          }
        case "2":
          try {
            userService.deleteAccount(me.getId());
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
    List<User> friends = userService.getFriends(me.getId()).stream().toList();
    if (friends.isEmpty()) {
      System.out.println("친구 : 없음");
      return;
    }

    String friendsStr = friends.stream().map(User::getEmail).collect(Collectors.joining(", "));
    System.out.println("친구 : " + friendsStr);
  }

  private void sendFriendRequest() {
    System.out.println("\nx. 뒤로가기");
    while (true) {
      String email = InputHandler.getValidEmail("친구 요청할 이메일 : ");
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

      if (me.getFriends().contains(receiver.getId())) {
        System.out.println("이미 친구입니다.");
        continue;
      }

      try {
        friendRequestService.sendFriendRequest(me.getId(), receiver.getId());
        System.out.println("친구 요청을 보냈습니다.");
        return;
      } catch (NoSuchElementException e) {
        System.out.println("유저를 찾을 수 없습니다.");
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void viewReceivedFriendRequests() {
    while (true) {
      List<FriendRequest> friendRequests = friendRequestService.getReceivedRequests(me.getId());

      if (friendRequests.isEmpty()) {
        System.out.println("\n받은 친구 요청이 없습니다.");
        return;
      }

      for (int i = 0; i < friendRequests.size(); i++) {
        FriendRequest fr = friendRequests.get(i);
        User sender = userService.findById(fr.getSenderId());
        System.out.println((i + 1) + ". " + sender.getUsername());
      }

      System.out.println("\nx. 뒤로가기");
      while (true) {
        String idxStr = InputHandler.getInputOrBack("선택 : ");
        if (idxStr == null) {
          return;
        }

        try {
          int idx = Integer.parseInt(idxStr);
          if (idx < 1 || idx > friendRequests.size()) {
            System.out.println("유효한 번호를 입력해주세요.");
            continue;
          }

          FriendRequest selected = friendRequests.get(idx - 1);

          Boolean accepted = InputHandler.getYesOrNo("친구 요청 수락");
          if (accepted == null) {
            return;
          }

          if (accepted) {
            friendRequestService.acceptFriendRequest(selected.getId());
            System.out.println("친구 요청을 수락했습니다.");
          } else {
            friendRequestService.declineFriendRequest(selected.getId());
            System.out.println("친구 요청을 거절했습니다.");
          }
          break;
        } catch (NumberFormatException e) {
          System.out.println("숫자를 입력해주세요.");
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      }
    }
  }

  private void viewSentFriendRequests() {
    while (true) {
      List<FriendRequest> friendRequests = friendRequestService.getSentRequests(me.getId());

      if (friendRequests.isEmpty()) {
        System.out.println("\n보낸 친구 요청이 없습니다.");
        return;
      }

      for (int i = 0; i < friendRequests.size(); i++) {
        FriendRequest fr = friendRequests.get(i);
        User receiver = userService.findById(fr.getReceiverId());
        System.out.println((i + 1) + ". " + receiver.getUsername());
      }

      System.out.println("\nx. 뒤로가기");
      String idxStr = InputHandler.getInputOrBack("취소할 요청 선택 : ");
      if (idxStr == null) {
        return;
      }

      try {
        int idx = Integer.parseInt(idxStr);
        if (idx < 1 || idx > friendRequests.size()) {
          System.out.println("유효한 번호를 입력해주세요.");
          continue;
        }

        FriendRequest selected = friendRequests.get(idx - 1);

        friendRequestService.declineFriendRequest(selected.getId());
        System.out.println("친구 요청을 취소했습니다.");
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void deleteFriend() {
    while (true) {
      List<User> friends = userService.getFriends(me.getId()).stream().toList();
      if (friends.isEmpty()) {
        System.out.println("친구 : 없음");
        return;
      }
      for (int i = 0; i < friends.size(); i++) {
        if (friends.get(i) != null) {
          System.out.printf(
              "%d. %s (%s)\n", i + 1, friends.get(i).getUsername(), friends.get(i).getEmail());
        }
      }
      System.out.println("\nx. 뒤로가기");
      String idxStr = InputHandler.getInputOrBack("삭제할 친구 번호: ");
      if (idxStr == null) {
        return;
      }

      try {
        int idx = Integer.parseInt(idxStr) - 1;
        if (idx < 0 || idx >= friends.size()) {
          System.out.println("유효한 번호를 입력해주세요.");
          continue;
        }

        User friend = friends.get(idx);
        userService.removeFriend(me.getId(), friend.getId());
        System.out.println("친구가 삭제되었습니다.");
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private void showGuilds() {
    List<Guild> guilds = guildService.findAll();
    printGuildList(guilds);
  }

  private void printGuildList(List<Guild> guilds) {
    if (guilds == null || guilds.isEmpty()) {
      System.out.println("\n존재하는 서버가 없습니다.");
      return;
    }
    System.out.println("서버 목록 : ");
    for (int i = 0; i < guilds.size(); i++) {
      System.out.println((i + 1) + ". " + guilds.get(i));
    }
  }

  private Integer selectGuildIndex(List<Guild> guilds, String prompt) {
    while (true) {
      String idxStr = InputHandler.getInputOrBack(prompt);
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
      String name = InputHandler.getInputOrBack("서버 이름 : ");
      if (name == null || name.isBlank()) {
        name = me.getUsername() + "님의 서버";
      }

      Boolean isPublic = InputHandler.getYesOrNo("공개 여부");
      if (isPublic == null) {
        return;
      }

      try {
        Guild guild = guildService.create(new Guild(name, isPublic, me.getId()));
        if (guild != null) {
          guildService.addMember(guild.getId(), me.getId());
          guildService.addChannel(
              guild.getId(), new Channel(guild.getId(), "일반", ChannelType.CHAT));
          guildService.addChannel(
              guild.getId(), new Channel(guild.getId(), "일반", ChannelType.VOICE));
          userService.addGuild(me.getId(), guild.getId());
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

    while (true) {
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
        System.out.println(e.getMessage());
      }
    }
  }

  private void joinGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = guildService.findDiscoverableGuilds();
    printGuildList(guilds);
    if (guilds == null || guilds.isEmpty()) {
      return;
    }

    Integer idx = selectGuildIndex(guilds, "들어갈 서버 번호 : ");
    if (idx == null) {
      return;
    }

    Guild guild = guilds.get(idx);
    if (guild.getMembers().containsKey(me.getId())) {
      System.out.println("이미 들어간 서버입니다.");
      return;
    }

    try {
      guildService.addMember(guild.getId(), me.getId());
      System.out.println(guild.getName() + " 서버에 입장했습니다.");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void exitGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = userService.getGuilds(me.getId()).stream().toList();
    printGuildList(guilds);
    if (guilds.isEmpty()) {
      System.out.println("🔍 입장한 서버가 없습니다.");
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
      System.out.println(e.getMessage());
    }
  }

  private void openGuild() {
    System.out.println("\nx. 뒤로가기");

    List<Guild> guilds = userService.getGuilds(me.getId()).stream().toList();
    printGuildList(guilds);
    if (guilds.isEmpty()) {
      System.out.println("🔍 입장한 서버가 없습니다.");
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

    List<UUID> members = new ArrayList<>(guild.getMembers().keySet());
    for (int i = 0; i < members.size(); i++) {
      User member = userService.findById(members.get(i));
      System.out.println((i + 1) + ". " + (member != null ? member.getEmail() : members.get(i)));
    }

    while (true) {
      try {
        String memberIdxStr = InputHandler.getInputOrBack("새로운 주인(멤버) 번호 : ");
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
        System.out.println(e.getMessage());
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

    String guildName = InputHandler.getInputOrBack("변경할 이름 : ");
    if (guildName == null) {
      return;
    }

    try {
      guildService.updateName(guild.getId(), guildName);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private void changeGuildPublic() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    System.out.println("\nx. 뒤로가기");
    System.out.println("현재 공개 여부 : " + (guild.isDiscoverable() ? "공개" : "비공개"));

    Boolean isPublic = InputHandler.getYesOrNo("공개 여부");
    if (isPublic != null) {
      guildService.updateDiscoverable(guild.getId(), isPublic);
    }
  }

  private void kickMember() {
    Guild guild = checkOwnershipAndReturnGuild();
    if (guild == null) {
      return;
    }

    Set<UUID> members = guild.getMembers().keySet();
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
        String indexStr = InputHandler.getInputOrBack("추방할 멤버 번호 : ");
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
        System.out.println(e.getMessage());
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
        String name = InputHandler.getInputOrBack("채널 이름 : ");
        if (name == null) {
          return;
        }

        System.out.println("채널 유형 : ");
        for (int i = 0; i < ChannelType.values().length; i++) {
          System.out.println((i + 1) + ". " + ChannelType.values()[i]);
        }

        int typeIdx = InputHandler.getMenuInput(ChannelType.values().length, "채널 유형 선택 : ");

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
        System.out.println(e.getMessage());
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
      String idxStr = InputHandler.getInputOrBack("수정할 채널 번호 : ");
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
        final String newName = InputHandler.getInputOrBack("새 채널 이름 : ");

        System.out.println("현재 채널 유형 : " + channel.getType());
        System.out.print("새 채널 유형 : ");
        System.out.println("1. 채팅");
        System.out.println("2. 음성");
        System.out.println("3. 포럼");
        String typeIdxStr = InputHandler.getInputOrBack("새 채널 번호 : ");
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
        System.out.println(e.getMessage());
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
      String idxStr = InputHandler.getInputOrBack("삭제할 채널 번호 : ");
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
        System.out.println(e.getMessage());
      }
    }
  }
}
