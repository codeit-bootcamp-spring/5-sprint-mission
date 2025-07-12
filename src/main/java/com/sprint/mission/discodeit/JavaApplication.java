package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.*;

public class JavaApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new JCFUserService();
    private static final ChannelService channelService = new JCFChannelService();
    private static final MessageService messageService = new JCFMessageService(userService, channelService);

    public static void main(String[] args) {
        while (true) {
            printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> createChannel();
                    case 3 -> createMessage();
                    case 4 -> viewUsers();
                    case 5 -> viewChannels();
                    case 6 -> viewMessages();
                    case 7 -> updateUser();
                    case 8 -> updateChannel();
                    case 9 -> updateMessage();
                    case 10 -> deleteUser();
                    case 11 -> deleteChannel();
                    case 12 -> deleteMessage();
                    case 0 -> {
                        System.out.println("종료합니다.");
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n==== 채팅 서비스 관리 시스템 ====");
        System.out.println("1. 사용자 생성");
        System.out.println("2. 채널 생성");
        System.out.println("3. 메시지 생성");
        System.out.println("4. 사용자 목록 조회");
        System.out.println("5. 채널 목록 조회");
        System.out.println("6. 메시지 목록 조회");
        System.out.println("7. 사용자 수정");
        System.out.println("8. 채널 수정");
        System.out.println("9. 메시지 수정");
        System.out.println("10. 사용자 삭제");
        System.out.println("11. 채널 삭제");
        System.out.println("12. 메시지 삭제");
        System.out.println("0. 종료");
        System.out.print("선택: ");
    }

    private static void createUser() {
        System.out.print("닉네임 입력: ");
        String nickname = scanner.nextLine();
        User user = userService.create(nickname);
        System.out.println("생성된 사용자 ID: " + user.getId());
    }

    private static void createChannel() {
        System.out.print("채널 이름 입력: ");
        String name = scanner.nextLine();
        Channel channel = channelService.create(name);
        System.out.println("생성된 채널 ID: " + channel.getId());
    }

    private static void createMessage() {
        System.out.print("메시지 내용 입력: ");
        String content = scanner.nextLine();
        System.out.print("사용자 ID 입력: ");
        UUID userId = UUID.fromString(scanner.nextLine());
        System.out.print("채널 ID 입력: ");
        UUID channelId = UUID.fromString(scanner.nextLine());
        try {
            Message message = messageService.create(content, userId, channelId);
            System.out.println("메시지 ID: " + message.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("생성 실패: " + e.getMessage());
        }
    }

    private static void viewUsers() {
        System.out.println("==== 사용자 목록 ====");
        userService.findAll().forEach(user ->
                System.out.println(user.getId() + " - " + user.getNickname()));
    }

    private static void viewChannels() {
        System.out.println("==== 채널 목록 ====");
        channelService.findAll().forEach(channel ->
                System.out.println(channel.getId() + " - " + channel.getName()));
    }

    private static void viewMessages() {
        System.out.println("==== 메시지 목록 ====");
        messageService.findAll().forEach(message ->
                System.out.println(message.getId() + " - " + message.getContent() +
                        " (User: " + message.getUserId() + ", Channel: " + message.getChannelId() + ")"));
    }

    private static void updateUser() {
        System.out.print("수정할 사용자 ID 입력: ");
        UUID id = UUID.fromString(scanner.nextLine());
        System.out.print("새 닉네임 입력: ");
        userService.update(id, scanner.nextLine());
    }

    private static void updateChannel() {
        System.out.print("수정할 채널 ID 입력: ");
        UUID id = UUID.fromString(scanner.nextLine());
        System.out.print("새 채널 이름 입력: ");
        channelService.update(id, scanner.nextLine());
    }

    private static void updateMessage() {
        System.out.print("수정할 메시지 ID 입력: ");
        UUID id = UUID.fromString(scanner.nextLine());
        System.out.print("새 메시지 내용 입력: ");
        messageService.update(id, scanner.nextLine());
    }

    private static void deleteUser() {
        System.out.print("삭제할 사용자 ID 입력: ");
        userService.delete(UUID.fromString(scanner.nextLine()));
    }

    private static void deleteChannel() {
        System.out.print("삭제할 채널 ID 입력: ");
        channelService.delete(UUID.fromString(scanner.nextLine()));
    }

    private static void deleteMessage() {
        System.out.print("삭제할 메시지 ID 입력: ");
        messageService.delete(UUID.fromString(scanner.nextLine()));
    }
}
