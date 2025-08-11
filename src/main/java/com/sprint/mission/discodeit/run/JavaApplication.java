package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.Scanner;
import java.util.UUID;

public class JavaApplication {
    private  final ChannelService channelService;
    private final UserService userService;
    private final MessageService messageService;
    private static final Scanner sc = new Scanner(System.in);

    public JavaApplication(String type) {
        switch (type.toLowerCase()) {
            case "jcf":
                this.userService = new JCFUserService();
                this.channelService = new JCFChannelService();
                this.messageService = new JCFMessageService();
                break;
            case "file":
                this.userService = new FileUserService();
                this.channelService = new FileChannelService();
                this.messageService = new FileMessageService();
                break;
            case "basic":
                this.userService = new BasicUserService(new JCFUserRepository()); // 예시
                this.channelService = new BasicChannelService(new JCFChannelRepository());
                this.messageService = new BasicMessageService(new JCFMessageRepository());
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 구현체 타입: " + type);
        }
    }

    public static void main(String[] args) {
        System.out.println("사용할 구현체를 입력하세요 (jcf / file / basic):");
        String type = sc.nextLine();
        JavaApplication app = new JavaApplication(type);
        app.mainMenu();
    }

    public void mainMenu() {
        System.out.println("==========디스코드==========");
        while (true) {

            System.out.println("***** 메인 메뉴 *****");
            System.out.println("1. 등록");
            System.out.println("2. 조회");
            System.out.println("3. 수정");
            System.out.println("4. 삭제");
            System.out.println("9. 종료");
            System.out.print("메뉴 번호 입력 : ");
            String num =sc.nextLine();
            switch (num) {
                case "1":
                    registerMenu();
                    break;
                case "2":
                    readMenu();
                    break;
                case "3":
                    modifyMenu();
                    break;
                case "4":
                    deleteMenu();
                    break;
                case "9":
                    System.out.println("프로그램 종료");
                    return;
                default:
                    System.out.println("잘못입력하였습니다. 다시입력해주세요");
            }

        }

    }

    public void registerMenu() {
        while (true) {
            System.out.println("=== 등록 ===");
            System.out.println("1. 등록 - 채널");
            System.out.println("2. 등록 - 사용자");
            System.out.println("3. 등록 - 메시지");
            System.out.println("9. 전 페이지로");
            System.out.print("메뉴 번호 입력 >> ");
            String num =sc.nextLine();
            switch (num){
                case "1":
                    inputChannel();
                    continue;
                case "2":
                    inputUser();
                    continue;
                case "3":
                    inputMessage();
                    continue;
                case "9":
                    return;
                default:
                    System.out.println("잘못입력하였습니다. 다시 입력해주세요.");
            }

        }
    }


    public void readMenu() {
        while (true) {
            System.out.println("=== 조회 ===");
            System.out.println("1. 단건 - 채널");
            System.out.println("2. 단건 - 사용자");
            System.out.println("3. 단건 - 메시지");
            System.out.println("-------------------");
            System.out.println("4. 다건 - 전체 채널");
            System.out.println("5. 다건 - 전체 사용자");
            System.out.println("6. 다건 - 전체 메시지");
            System.out.println("9. 전 페이지로");
            System.out.print("메뉴 번호 입력 >> ");
            String num =sc.nextLine();
            switch (num){
                case "1":
                    oneChannel();
                    continue;
                case "2":
                    oneUser();
                    continue;
                case "3":
                    oneMessage();
                    continue;
                case "4":
                    allChannel();
                    continue;
                case "5":
                    allUser();
                    continue;
                case "6":
                    allMessage();
                    continue;

                case "9":
                    return;
                default:
                    System.out.println("잘못입력하였습니다. 다시 입력해주세요.");
            }

        }
    }

    public void modifyMenu() {
        while (true) {
            System.out.println("=== 수정 ===");
            System.out.println("1. 수정 - 채널");
            System.out.println("2. 수정 - 사용자");
            System.out.println("3. 수정 - 메시지");
            System.out.println("9. 전 페이지로");
            System.out.print("메뉴 번호 입력 >> ");
            String num =sc.nextLine();
            switch (num){
                case "1":
                    updateChannel();
                    continue;
                case "2":
                    updateUser();
                    continue;
                case "3":
                    updateMessage();
                    continue;
                case "9":
                    return;
                default:
                    System.out.println("잘못입력하였습니다. 다시 입력해주세요.");
            }

        }
    }

    public void deleteMenu() {
        while (true) {
            System.out.println("=== 삭제 ===");
            System.out.println("1. 삭제 - 채널");
            System.out.println("2. 삭제 - 사용자");
            System.out.println("3. 삭제 - 메시지");
            System.out.println("9. 전 페이지로");
            System.out.print("메뉴 번호 입력 >> ");
            String num =sc.nextLine();
            switch (num){
                case "1":
                    deleteChannel();
                    continue;
                case "2":
                    deleteUser();
                    continue;
                case "3":
                    deleteMessage();
                    continue;
                case "9":
                    return;
                default:
                    System.out.println("잘못입력하였습니다. 다시 입력해주세요.");
            }

        }

    }

    public void inputChannel(){
        System.out.println("=== 등록 - 채널===");
        System.out.print("채널 이름 :");
        String name=sc.nextLine();
        System.out.print("채널 내용 :");
        String description=sc.nextLine();
        channelService.createChannel(name,description);
        System.out.println("성공적으로 생성 완료하였습니다.");
    }
    public void inputUser() {
        System.out.println("=== 등록 - 사용자===");
        System.out.print("별명 :");
        String name = sc.nextLine();
        System.out.print("비밀번호 :");
        String password = sc.nextLine();
        userService.createUser(name,password);
    }

    public void inputMessage(){
        System.out.println("=== 등록 - 메시지===");
        System.out.print("사용자 UUID :");
        UUID name= null;
        try {
            name = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        System.out.print("채널 UUId :");
        UUID channel= null;
        try {
            channel = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        System.out.print("메시지 내용 :");
        String content=sc.nextLine();
        if (userService.readByIdUser(name) !=null && channelService.readByIdChannel(channel)!=null){
            messageService.createMessage(name, channel, content);
            System.out.println("메시지 등록에 성공하였습니다.");
        }else{
            System.out.println("사용자명이나 채널명이 존재하지 않습니다.");
        }

    }

    public void oneChannel(){
        System.out.println("=== 단건 - 채널 ===");
        System.out.print("채널 UUID :");
        UUID name= null;
        try {
            name = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        Channel channel=channelService.readByIdChannel(name);
        if(channel!=null){
            System.out.println(channel);
        }else{
            System.out.println("채널 조회에 실패하였습니다.");
        }
    }
    public void oneUser(){
        System.out.println("=== 단건 - 사용자 ===");
        System.out.print("사용자 UUID :");
        UUID name= null;
        try {
            name = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        User user=userService.readByIdUser(name);
        if(user!=null){
            System.out.println(user);
        }else{
            System.out.println("사용자 조회에 실패하였습니다.");
        }
    }
    public void oneMessage(){
        System.out.println("=== 단건 - 메시지 ===");
        System.out.print("메시지 UUID :");
        UUID name= null;
        try {
            name = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        Message message=messageService.readByIdMessage(name);
        if(message!=null){
            System.out.println(message);
        }else{
            System.out.println("메시지 조회에 실패하였습니다.");
        }
    }

    public void allChannel(){
        System.out.println("=== 다건 - 전체 채널 ===");
        channelService.readAllChannel();
    }
    public void allUser(){
        System.out.println("=== 다건 - 전체 사용자 ===");
        userService.readAllUser();
    }
    public void allMessage(){
        System.out.println("=== 다건 - 전체 메시지 ===");
        messageService.readAllMessage();
    }


    public void updateChannel(){
        System.out.println("=== 수정 - 채널 ===");
        System.out.print("채널 UUID : ");
        UUID channelUUID = null;
        try {
            channelUUID = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        System.out.print("채널 이름 :");
        String channelName = sc.nextLine();
        System.out.print("채널 설명 : ");
        String description = sc.nextLine();
        channelService.updateChannel(channelUUID,channelName,description);
    }

    public void updateUser(){
        System.out.println("=== 수정 - 사용자 ===");
        System.out.print("사용자 UUID : ");
        UUID userUUID = null;
        try {
            userUUID = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        System.out.print("사용자 이름 :");
        String userName = sc.nextLine();
        System.out.print("비밀번호 : ");
        String password = sc.nextLine();
        userService.updateUser(userUUID,userName, password);

    }
    public void updateMessage(){
        System.out.println("=== 수정 - 메시지 ===");
        System.out.print("메시지 UUID : ");
        UUID messageUUID = null;
        try {
            messageUUID = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        System.out.print("메시지 내용 :");
        String content = sc.nextLine();
        messageService.updateMessage(messageUUID, content);
    }

    public void deleteChannel(){
        System.out.println("=== 삭제 - 채널 ===");
        System.out.print("채널 UUID : ");
        UUID channelUUID = null;
        try {
            channelUUID = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        channelService.deleteByIdChannel(channelUUID);
    }
    public void deleteMessage(){
        System.out.println("=== 삭제 - 메시지 ===");
        System.out.print("메시지 UUID : ");
        UUID messageUUID = null;
        try {
            messageUUID = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        messageService.deleteByIdMessage(messageUUID);
    }
    public void deleteUser(){
        System.out.println("=== 삭제 - 사용자 ===");
        System.out.print("사용자 UUID : ");
        UUID userUUID = null;
        try {
            userUUID = UUID.fromString(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("올바른 UUID 형식을 입력해주세요");
            return;
        }
        userService.deleteByIdUser(userUUID);
    }

}
