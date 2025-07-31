package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.*;

public class JavaApplication {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserService userService = new JCFUserService();
    private static final ChannelService channelService = new JCFChannelService();
    private static final MessageService messageService = new JCFMessageService(userService, channelService);

    public static void main(String[] args) {
        while(true){
            printMainMenu();
            try{
                int choice = Integer.parseInt(sc.nextLine());
                switch(choice){
                    case 1 -> UserMenu(); // 유저 관련 메뉴
                    case 2 -> ChannelMenu(); //채널 관련 메뉴
                    case 3 -> MessageMenu(); //메시지 관련 메뉴
                    case 0 -> {
                        System.out.println("종료합니다.");
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } catch (Exception e){
                System.out.println("오류 발생: " + e.getMessage());
            }
        }
    }

    private static void printMainMenu(){
        System.out.println("1. 사용자 메뉴");
        System.out.println("2. 채널 메뉴");
        System.out.println("3. 메시지 메뉴");
        System.out.println("0. 종료");
        System.out.print("선택: ");
    }

    private static void UserMenu(){
        while(true){
            printUserMenu();
            try{
                int choice = Integer.parseInt(sc.nextLine());
                switch(choice){
                    case 1 -> createUser();
                    case 2 -> deleteUser();
                    case 3 -> updateUser();
                    case 4 -> findUser();
                    case 5 -> viewUser();
                    case 0 -> {
                        return; //해당 while루프를 종료시키고 메인으로 되돌아감.
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
        }
    }

    private static void printUserMenu(){
        System.out.println("1. 사용자 생성");
        System.out.println("2. 사용자 삭제");
        System.out.println("3. 사용자 업데이트");
        System.out.println("4. 사용자 찾기");
        System.out.println("5. 사용자 목록");
        System.out.println("0. 뒤로가기");
    }

    private static void createUser(){
        System.out.println("닉네임 입력: ");
        String nickname = sc.nextLine();
        User user = userService.create(nickname);
        System.out.println("생성된 사용자 ID: " + user.getId());
    }
    private static void deleteUser(){
        System.out.println("삭제할 사용자 ID입력: ");
        userService.delete(UUID.fromString(sc.nextLine()));
    }
    private static void updateUser(){
        System.out.print("수정할 사용자 ID입력: ");
        UUID id = UUID.fromString(sc.nextLine());
        System.out.print("새 닉네임 입력: ");
        userService.update(id,sc.nextLine());
    }
    private static void findUser(){
        System.out.print("찾고싶은 사용자 ID입력: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            User user = userService.findById(id);
            System.out.println("사용자 정보:");
            System.out.println("ID: " + user.getId());
            System.out.println("닉네임: " + user.getNickname());
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 ID 형식입니다.");
        }
    }
    private static void viewUser(){
        System.out.println("==== 사용자 목록 ====");
        userService.findAll().forEach(user ->
                System.out.println(user.getId() + " - " + user.getNickname()));
    }

    private static void ChannelMenu(){
        while(true){
            printChannelMenu();
            try{
                int choice = Integer.parseInt(sc.nextLine());
                switch(choice){
                    case 1 -> createChannel();
                    case 2 -> deleteChannel();
                    case 3 -> updateChannel();
                    case 4 -> findChannel();
                    case 5 -> viewChannel();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
        }
    }

    private static void printChannelMenu(){
        System.out.println("1. 채널 생성");
        System.out.println("2. 채널 삭제");
        System.out.println("3. 채널 업데이트");
        System.out.println("4. 채널 찾기");
        System.out.println("5. 채널 목록");
        System.out.println("0. 뒤로가기");
    }

    private static void createChannel(){
        System.out.println("채널 이름 입력: ");
        String CHname = sc.nextLine();
        Channel channel = channelService.create(CHname);
        System.out.println("생성된 채널 ID: " + channel.getId());
    }
    private static void deleteChannel(){
        System.out.println("삭제할 채널 ID입력: ");
        channelService.delete(UUID.fromString(sc.nextLine()));
    }
    private static void updateChannel(){
        System.out.print("수정할 채널 ID입력: ");
        UUID id = UUID.fromString(sc.nextLine());
        System.out.print("새 채널 이름 입력: ");
        channelService.update(id,sc.nextLine());
    }
    private static void findChannel(){
        System.out.print("찾고싶은 채널 ID입력: ");
        try {
            UUID id = UUID.fromString(sc.nextLine());
            Channel channel = channelService.findById(id);
            System.out.println("채널 정보:");
            System.out.println("ID: " + channel.getId());
            System.out.println("채널 이름: " + channel.getchName());
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 ID 형식입니다.");
        }
    }
    private static void viewChannel(){
        System.out.println("==== 채널 목록 ====");
        channelService.findAll().forEach(channel ->
                System.out.println(channel.getId() + " - " + channel.getchName()));
    }


    private static void MessageMenu(){
        while(true){
            printMessageMenu();
            try{
                int choice = Integer.parseInt(sc.nextLine());
                switch(choice){
                    case 1 -> createMessage();
                    case 2 -> deleteMessage();
                    case 3 -> updateMessage();
                    case 4 -> findKeyMessage();
                    case 5 -> findUserMessage();
                    case 6 -> viewAllMessage();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
        }
    }

    private static void printMessageMenu(){
        System.out.println("1. 메시지 생성");
        System.out.println("2. 메시지 삭제");
        System.out.println("3. 메시지 업데이트");
        System.out.println("4. 키값으로 메시지 찾기");
        System.out.println("5. 유저별 메시지 목록");
        System.out.println("6. 모든 메시지 목록");
        System.out.println("0. 뒤로가기");
    }

    private static void createMessage(){
        System.out.println("메시지 키 입력: ");
        String messageKey = sc.nextLine();
        System.out.print("메시지 내용 입력: ");
        String messageText = sc.nextLine();
        System.out.println("사용자 ID 입력: ");
        UUID userId = UUID.fromString(sc.nextLine());
        System.out.print("채널 ID 입력: ");
        UUID channelId = UUID.fromString(sc.nextLine());
        try {
            Message message = messageService.create(userId, channelId, messageKey, messageText);
            System.out.println("메시지 ID: " + message.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("생성 실패: " + e.getMessage());
        }
    }
    private static void deleteMessage(){
        System.out.println("삭제할 메시지 키 입력: ");
        String messageKey = sc.nextLine();
        messageService.delete(messageKey);
    }
    private static void updateMessage(){
        System.out.print("수정할 메시지 키 입력: ");
        String messageKey = sc.nextLine();
        System.out.print("새 메시지 입력: ");
        String newMessage = sc.nextLine();
        try {
            messageService.update(messageKey, newMessage); // messageKey 사용
            System.out.println("메시지가 수정되었습니다.");
        } catch (NoSuchElementException e) {
            System.out.println("수정 실패: " + e.getMessage());
        }
    }
    private static void findKeyMessage(){
        System.out.print("찾고싶은 메시지 키 입력: ");
        try {
            String messageKey = sc.nextLine();
            Message message = messageService.findMessageKey(messageKey);
            System.out.println("메시지 정보:");
            System.out.println("소속 채널:" + message.getChId());
            System.out.println("유저:" + message.getUserId());
            System.out.println("키: " + message.getMessageKey());
            System.out.println("메시지 내용: " + message.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("해당 메시지를 찾을 수 없습니다.");
        }
    }

    private static void findUserMessage() {
        System.out.print("메시지를 조회할 사용자 ID 입력: ");
        try {
            UUID userId = UUID.fromString(sc.nextLine());
            List<Message> messages = messageService.findByUserId(userId);
            if (messages.isEmpty()) {
                System.out.println("해당 사용자의 메시지가 없습니다.");
            } else {
                for (Message msg : messages) {
                    System.out.println("메시지 키: " + msg.getMessageKey());
                    System.out.println("내용: " + msg.getMessage());
                    System.out.println("작성 시각: " + msg.getCreatedAt());
                    System.out.println("--------------------------");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 UUID 형식입니다.");
        }
    }
    private static void viewAllMessage(){
        System.out.println("==== 메시지 목록 ====");
        List<Message> messages = messageService.findAll();

        if (messages.isEmpty()) {
            System.out.println("등록된 메시지가 없습니다.");
        } else {
            for (Message msg : messages) {
                System.out.println("메시지 ID: " + msg.getId());
                System.out.println("유저 ID: " + msg.getUserId());
                System.out.println("채널 ID: " + msg.getChId());
                System.out.println("메시지 키: " + msg.getMessageKey());
                System.out.println("내용: " + msg.getMessage());
                System.out.println("작성 시각: " + msg.getCreatedAt());
                System.out.println("--------------------------");
            }
        }
    }

}
