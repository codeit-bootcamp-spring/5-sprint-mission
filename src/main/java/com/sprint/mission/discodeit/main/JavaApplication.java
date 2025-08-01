package com.sprint.mission.discodeit.main;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class JavaApplication {

    private static final Scanner sc = new Scanner(System.in);

    static ChannelRepository channelRepository = new FileChannelRepository();
    static MessageRepository messageRepository = new FileMessageRepository();

    private static final UserService userService = new BasicUserService();
    private static final MessageService messageService = new BasicMessageService(messageRepository, userService);
    private static final ChannelService channelService = new BasicChannelService(channelRepository, messageService);

    private static User testUser = null;

    public static void main(String[] args) {
        System.out.println("===테스트용 아이디를 생성합니다===");
        userService.createUser("홍길동", "email@1234", "1234");
        testUser = userService.findUser("홍길동");

        while (true) {
            System.out.println("======메인 메뉴=====");
            System.out.println("1. 사용자 메뉴");
            System.out.println("2. 채널 메뉴");
            System.out.println("3. 메시지 메뉴");
            System.out.println("0. 프로그램 종료");
            System.out.print("번호를 입력하세요: ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    userMenu();
                    break;
                case "2":
                    channelMenu();
                    break;
                case "3":
                    messageMenu();
                    break;
                case "0":
                    System.out.println("프로그램을 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
    }

    private static void userMenu() {
        while (true) {
            System.out.println("=====사용자 메뉴=====");
            System.out.println("1. 사용자 생성");
            System.out.println("2. 사용자 조회");
            System.out.println("3. 사용자 전체 조회");
            System.out.println("4. 사용자 정보 수정");
            System.out.println("5. 사용자 삭제");
            System.out.println("9. 뒤로가기");
            System.out.println("0. 프로그램 종료");
            System.out.print("번호를 입력하세요: ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    System.out.print("ID를 입력해주세요: ");
                    String username = sc.nextLine();
                    System.out.print("email을 입력해주세요: ");
                    String email = sc.nextLine();
                    System.out.print("비밀번호를 입력해주세요: ");
                    String password = sc.nextLine();
                    userService.createUser(username, email, password);
                    break;
                case "2":
                    System.out.print("조회할 ID를 입력해주세요: ");
                    String searchUsername = sc.nextLine();
                    User user = userService.findUser(searchUsername);
                    if (user != null) System.out.println(user);
                    break;
                case "3":
                    List<User> users = userService.findAllUsers();
                    users.forEach(System.out::println);
                    break;
                case "4":
                    System.out.print("수정할 사용자의 UUID를 입력해주세요: ");
                    try {
                        UUID id = UUID.fromString(sc.nextLine());
                        System.out.print("새 ID를 입력해주세요: ");
                        String newUsername = sc.nextLine();
                        System.out.print("새 비밀번호를 입력해주세요: ");
                        String newPassword = sc.nextLine();
                        userService.updateUser(id, newUsername, newPassword);
                    } catch (IllegalArgumentException e) {
                        System.out.println("UUID 형식이 올바르지 않습니다.");
                    }
                    break;
                case "5":
                    System.out.print("삭제할 사용자의 UUID를 입력해주세요: ");
                    try {
                        UUID id = UUID.fromString(sc.nextLine());
                        userService.deleteUser(id);
                    } catch (IllegalArgumentException e) {
                        System.out.println("UUID 형식이 올바르지 않습니다.");
                    }
                    break;
                case "9":
                    return;
                case "0":
                    System.exit(0);
                default:
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
    }

    private static void channelMenu() {
        while (true) {
            System.out.println("=====채널 메뉴=====");
            System.out.println("1. 채널 생성");
            System.out.println("2. 채널 조회");
            System.out.println("3. 채널 전체 조회");
            System.out.println("4. 채널명 수정");
            System.out.println("5. 채널 삭제");
            System.out.println("9. 뒤로가기");
            System.out.println("0. 프로그램 종료");
            System.out.print("번호를 입력하세요: ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    System.out.print("생성할 채널명을 입력해주세요: ");
                    String channelName = sc.nextLine();
                    channelService.createChannel(channelName, testUser.getId().toString());
                    break;
                case "2":
                    System.out.print("조회할 채널명을 입력해주세요: ");
                    String searchName = sc.nextLine();
                    Channel channel = channelService.findByChannelName(searchName);
                    if (channel != null) {
                        UUID userId = UUID.fromString(channel.getCreatorUser());
                        User creator = userService.findUserById(userId);
                        String username = creator != null ? creator.getUsername() : "삭제된 유저";

                        System.out.printf("채널명: %s, 생성자: %s, 생성일: %d, 수정일: %d%n",
                                channel.getChannelName(), username, channel.getCreatedAt(), channel.getUpdatedAt());
                    }
                    break;
                case "3":
                    List<Channel> channels = channelService.findAllChannels();
                    System.out.println("=== 채널 목록 ===");
                    for (Channel ch : channels) {
                        UUID userId = UUID.fromString(ch.getCreatorUser());
                        User creator = userService.findUserById(userId);
                        String username = creator != null ? creator.getUsername() : "삭제된 유저";

                        System.out.printf("- 채널명: %s (생성자: %s)%n", ch.getChannelName(), username);
                    }
                    break;
                case "4":
                    System.out.print("수정할 채널명을 입력해주세요: ");
                    String newName = sc.nextLine();
                    channelService.updateChannel(newName);
                    break;
                case "5":
                    System.out.print("삭제할 채널명을 입력해주세요: ");
                    String delName = sc.nextLine();
                    channelService.deleteChannel(delName);
                    break;
                case "9":
                    return;
                case "0":
                    System.exit(0);
                default:
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
    }

    private static void messageMenu() {
        while (true) {
            System.out.println("======메시지 메뉴=====");
            System.out.println("1. 메시지 생성");
            System.out.println("2. 메시지 조회");
            System.out.println("3. 메시지 전체 조회");
            System.out.println("4. 메시지 수정");
            System.out.println("5. 메시지 삭제");
            System.out.println("9. 뒤로가기");
            System.out.println("0. 프로그램 종료");
            System.out.print("번호를 입력하세요: ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    System.out.print("생성할 메시지 내용을 입력해주세요: ");
                    String content = sc.nextLine();

                    List<Channel> allChannels = channelService.findAllChannels();
                    if (allChannels.isEmpty()) {
                        System.out.println("등록된 채널이 없습니다. 먼저 채널을 생성해주세요.");
                        return;
                    } else {
                        System.out.println("현재 등록된 채널 목록:");
                        for (Channel ch : allChannels) {
                            System.out.println("- " + ch.getChannelName());
                        }
                    }
                    System.out.print("메시지를 보낼 채널명을 입력해주세요: ");
                    String channelName = sc.nextLine();

                    Channel channel = channelService.findByChannelName(channelName);
                    if (channel == null) {
                        System.out.println("해당하는 채널이 없습니다.");
                        break;
                    }
                    messageService.createMessage(content, channelName, testUser.getId().toString());
                    break;
                case "2":
                    System.out.print("조회할 메시지의 UUID를 입력해주세요: ");
                    try {
                        UUID msgId = UUID.fromString(sc.nextLine());
                        Message message = messageService.findMessage(msgId);
                        if (message != null) {
                            UUID userId = UUID.fromString(message.getCreatorUserId());
                            User creator = userService.findUserById(userId);
                            String username = creator != null ? creator.getUsername() : "삭제된 유저";
                            System.out.printf("채널명: %s, 작성자: %s, 내용: %s, 생성일: %d, 수정일: %d%n",
                                    message.getChannelName(), username, message.getContent(),
                                    message.getCreatedAt(), message.getUpdatedAt());
                        } else {
                            System.out.println("해당 메시지가 없습니다.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("UUID 형식이 올바르지 않습니다.");
                    }

                    break;

                case "3":
                    List<Message> messages = messageService.findAllMessages();
                    System.out.println("=== 메시지 목록 ===");
                    if (messages.isEmpty()) {
                        System.out.println("저장된 메시지가 없습니다.");
                        break;
                    }
                    for (Message msg : messages) {
                        String username = "이름 없음";
                        try {
                            UUID userId = UUID.fromString(msg.getCreatorUserId());
                            User creator = userService.findUserById(userId);
                            if (creator != null) {
                                username = creator.getUsername();
                            }
                        } catch (IllegalArgumentException e) {
                        }
                        System.out.printf("- [%s] %s: %s%n", msg.getChannelName(), username, msg.getContent());
                    }
                    break;
                case "4":
                    System.out.print("수정할 메시지의 UUID를 입력해주세요: ");
                    try {
                        UUID targetMsgId = UUID.fromString(sc.nextLine());

                        Message target = messageService.findAllMessages().stream()
                                .filter(m -> m.getMsgId().equals(targetMsgId))
                                .findFirst()
                                .orElse(null);

                        if (target != null) {
                            System.out.print("새 메시지 내용을 입력해주세요: ");
                            String newMsg = sc.nextLine();
                            messageService.updateMessage(targetMsgId, newMsg);
                            System.out.println("메시지가 성공적으로 수정되었습니다.");
                        } else {
                            System.out.println("해당 UUID를 가진 메시지를 찾을 수 없습니다.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("UUID 형식이 올바르지 않습니다.");
                    }
                    break;

                case "5":
                    System.out.print("삭제할 메시지의 UUID를 입력해주세요: ");
                    try {
                        UUID delMsgId = UUID.fromString(sc.nextLine());
                        Message target = messageService.findAllMessages().stream()
                                .filter(m -> m.getMsgId().equals(delMsgId))
                                .findFirst()
                                .orElse(null);

                        if (target != null) {
                            messageService.deleteMessage(delMsgId); // 또는 UUID 기반 메서드로 변경
                            System.out.println("메시지가 성공적으로 삭제되었습니다.");
                        } else {
                            System.out.println("해당 UUID를 가진 메시지를 찾을 수 없습니다.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("UUID 형식이 올바르지 않습니다.");
                    }
                    break;

                case "9":
                    return;
                case "0":
                    System.exit(0);
                default:
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
    }
}
