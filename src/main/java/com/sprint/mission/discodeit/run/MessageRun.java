package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class MessageRun {
    private final Scanner sc;
    private final MessageService messageService;
    private final UserService userService;
    private final ChannelService channelService;

    public MessageRun(Scanner sc, MessageService messageService, UserService userService, ChannelService channelService) {
        this.sc = sc;
        this.messageService = messageService;
        this.userService = userService;
        this.channelService = channelService;
    }

    public void run() {
        while (true) {
            System.out.println("\n[메시지 서비스 메뉴]");
            System.out.println("1. 메시지 등록");
            System.out.println("2. 메시지 조회");
            System.out.println("3. 전체 메시지 조회");
            System.out.println("4. 메시지 수정");
            System.out.println("5. 메시지 삭제");
            System.out.println("9. 뒤로 가기");
            System.out.print("선택: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
                continue;
            }

            switch (choice) {
                case 1 -> createMessage();
                case 2 -> getMessage();
                case 3 -> getAllMessages();
                case 4 -> updateMessage();
                case 5 -> deleteMessage();
                case 9 -> {
                    System.out.println("메인 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void createMessage() {
        try {
            System.out.print("유저명: ");
            String userName = sc.nextLine();
            System.out.print("채널명: ");
            String channelName = sc.nextLine();

            User user = userService.get(userName);
            Channel channel = channelService.get(channelName);

            if (user == null) {
                System.out.println("존재하지 않는 유저입니다.");
                return;
            }
            if (channel == null) {
                System.out.println("존재하지 않는 채널입니다.");
                return;
            }

            System.out.print("내용 입력: ");
            String content = sc.nextLine();
            Message newMessage = new Message(user.getId(), channel.getId(), content);
            messageService.create(newMessage);

            System.out.println("등록 완료:\n[유저명: " + user.getName() + "]\n[채널: " + channel.getName() + "]\n[메시지 내용: " + newMessage.getContent() + "]");
        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
        }
    }

    private void getMessage() {
        System.out.print("조회할 메시지 내용: ");
        String content = sc.nextLine().trim();
        Message message = messageService.get(content);

        if (message == null) {
            System.out.println("해당 내용의 메시지 없음");
        } else {
            System.out.println("메시지 정보: " + message);
        }
    }

    private void getAllMessages() {
        List<Message> messages = messageService.getAll();
        if (messages.isEmpty()) {
            System.out.println("등록된 메시지 없음");
            return;
        }
        System.out.println("전체 메시지 목록:");
        for (Message m : messages) {
            System.out.println("-------------------------------------------");
            System.out.println(m);
            System.out.println("-------------------------------------------");
        }
    }

    private void updateMessage() {
        System.out.print("수정할 메시지 내용: ");
        String content = sc.nextLine().trim();
        Message message = messageService.get(content);

        if (message == null) {
            System.out.println("해당 내용의 메시지 없음");
            return;
        }

        System.out.print("새 메시지 내용: ");
        String newContent = sc.nextLine();
        message.update(newContent);
        messageService.update(message);
        System.out.println("수정 완료");
    }

    private void deleteMessage() {
        System.out.print("삭제할 메시지 내용: ");
        String content = sc.nextLine().trim();
        Message message = messageService.get(content);

        if (message == null) {
            System.out.println("해당 메시지를 찾을 수 없습니다.");
            return;
        }

        UUID messageId = message.getId();
        messageService.delete(messageId);
        System.out.println("삭제 완료");
    }
}
