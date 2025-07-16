package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MessageRun {
    private final Scanner sc;
    private final MessageService messageService;
    private final UserService userService;
    private final ChannelService channelService;

    public MessageRun(Scanner sc,
                      MessageService messageService,
                      UserService userService,
                      ChannelService channelService) {
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

    /* ========== CRUD 로직 ========== */

    private void createMessage() {
        System.out.print("유저 이름: ");
        String userName = sc.nextLine();
        System.out.print("비밀번호: ");
        String pw = sc.nextLine();

        User user = userService.get(userName);
        if (user == null || !user.getPassword().equals(pw)) {
            System.out.println("유저 이름 또는 비밀번호가 틀렸습니다.");
            return;
        }
        System.out.print("채널명: ");
        String channelName = sc.nextLine();
        Channel channel = channelService.get(channelName);
        if (channel == null) {
            System.out.println("존재하지 않는 채널입니다.");
            return;
        }

        System.out.print("메시지 내용: ");
        String content = sc.nextLine();

        Message msg = new Message(user.getId(), channel.getId(), content);
        messageService.create(msg);

        System.out.println("등록 완료!\n[채널: " + channel.getName() + "]\n[내용: " + msg.getContent() + "]");
    }

    private void getMessage() {
        System.out.print("조회할 메시지 내용: ");
        String content = sc.nextLine().trim();

        List<Message> messages = messageService.getAll();
        boolean found = false;

        for (Message msg : messages) {
            if (msg.getContent().contains(content)) {
                User user = userService.get(msg.getUserId());
                Channel channel = channelService.get(msg.getChannelId());

                System.out.println("-------------------------------------------");
                System.out.println("내용: " + msg.getContent());
                System.out.println("작성자: " + (user != null ? user.getName() : "알 수 없음"));
                System.out.println("채널: " + (channel != null ? channel.getName() : "알 수 없음"));
                System.out.println("작성 시각: " + new Date(msg.getCreatedAt()));
                found = true;
            }
        }

        if (!found) {
            System.out.println("해당 내용을 포함한 메시지 없음");
        } else {
            System.out.println("-------------------------------------------");
        }
    }

    private void getAllMessages() {
        List<Message> messages = messageService.getAll();
        if (messages.isEmpty()) {
            System.out.println("등록된 메시지 없음");
            return;
        }
        System.out.println("\n<전체 메시지 목록>");
        for (Message m : messages) {
            System.out.println("-------------------------------------------");
            System.out.println(m);
        }
        System.out.println("-------------------------------------------");
    }

    private void updateMessage() {
        System.out.print("수정할 메시지 내용: ");
        String oldContent = sc.nextLine().trim();
        Message msg = messageService.get(oldContent);
        if (msg == null) {
            System.out.println("메시지를 찾을 수 없습니다.");
            return;
        }

        User author = userService.get(msg.getUserId());
        if (!passwordMatch(author)) return;

        System.out.print("새 메시지 내용: ");
        String newContent = sc.nextLine();
        msg.update(newContent);
        messageService.update(msg);
        System.out.println("수정 완료");
    }

    private void deleteMessage() {
        System.out.print("삭제할 메시지 내용: ");
        String content = sc.nextLine().trim();
        Message msg = messageService.get(content);
        if (msg == null) {
            System.out.println("메시지를 찾을 수 없습니다.");
            return;
        }

        User author = userService.get(msg.getUserId());
        if (!passwordMatch(author)) return;

        messageService.delete(msg.getId());
        System.out.println("삭제 완료");
    }

    private boolean passwordMatch(User user) {
        System.out.print("비밀번호 확인: ");
        String pw = sc.nextLine();
        if (!user.getPassword().equals(pw)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false;
        }
        return true;
    }
}

