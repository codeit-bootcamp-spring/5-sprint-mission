package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel; // Channel 도메인 모델 import
import com.sprint.mission.discodeit.entity.Message; // Message 도메인 모델 import
import com.sprint.mission.discodeit.entity.User; // User 도메인 모델 import
import com.sprint.mission.discodeit.service.ChannelService; // ChannelService 인터페이스 import
import com.sprint.mission.discodeit.service.MessageService; // MessageService 인터페이스 import
import com.sprint.mission.discodeit.service.UserService; // UserService 인터페이스 import

import java.util.List; // List 인터페이스 import
import java.util.Optional; // Optional 클래스 import
import java.util.UUID; // UUID 클래스 import

import java.nio.file.*; //File 구현체 import
import java.io.IOException; //File 구현체 import


 //애플리케이션의 메인 클래스.
 //각 도메인 서비스의 CRUD 기능을 테스트하고, 심화 요구사항인 의존성 주입 및 검증 로직을 시연한다.

public class JavaApplication {

    // 폴더 내 모든 .ser 파일 삭제
    public static void clearDataFolder(String folderName) {
        Path dir = Paths.get(System.getProperty("user.dir"), folderName);
        if (Files.exists(dir)) {
            try {
                Files.list(dir)
                        .filter(p -> p.getFileName().toString().endsWith(".ser"))
                        .forEach(path -> {
                            try { Files.delete(path); } catch (IOException e) { }
                        });
            } catch (IOException e) { /* 무시 가능 */ }
        }
    }

    public static void main(String[] args) {

        // 1. 데이터 폴더 정리 (user, channel, message)
        clearDataFolder("user_data");
        clearDataFolder("channel_data");
        clearDataFolder("message_data");

        // ServiceFactory를 통해 서비스 인스턴스들을 가져온다. (싱글톤 패턴 활용)
        ServiceFactory factory = ServiceFactory.getInstance();
        UserService userService = factory.getUserService();
        ChannelService channelService = factory.getChannelService();
        MessageService messageService = factory.getMessageService();

        System.out.println("--- 사용자(User) 서비스 테스트 시작 ---");

        // 1. 사용자 등록 (Create)
        User user1 = new User("Alice", "alice@example.com");
        User user2 = new User("Bob", "bob@example.com");
        User user3 = new User("Charlie", "charlie@example.com");

        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        System.out.println("\n[등록] 사용자 3명 등록 완료.");

        // 2. 사용자 조회 (단건 - Read by ID)
        Optional<User> foundUser1 = userService.findById(user1.getUserId());
        foundUser1.ifPresent(user -> System.out.println("\n[조회] 단건 조회 (Alice): " + user));

        // 3. 사용자 조회 (다건 - Read All)
        List<User> allUsers = userService.findAll();
        System.out.println("\n[조회] 모든 사용자 조회:");
        allUsers.forEach(System.out::println);

        // 4. 사용자 수정 (Update)
        // User 객체 자체의 update 메서드를 호출하여 필드를 변경합니다.
        user1.updateUser("Alicia", "alicia_new@example.com");
        // 서비스의 update 메서드를 호출하여 변경된 내용을 반영하고, 저장소에 업데이트합니다.
        Optional<User> updatedUser1 = userService.updateId(user1.getUserId(), user1);
        updatedUser1.ifPresent(user -> System.out.println("\n[수정] Alice -> Alicia로 수정 완료: " + user));

        // 5. 수정된 데이터 조회 확인
        Optional<User> reFoundUser1 = userService.findById(user1.getUserId());
        reFoundUser1.ifPresent(user -> System.out.println("[조회] 수정 후 Alicia 재조회: " + user));

        // 6. 사용자 삭제 (Delete)
        boolean deleted = userService.deleteById(user2.getUserId());
        System.out.println("\n[삭제] Bob 삭제 결과: " + (deleted ? "성공" : "실패"));

        // 7. 삭제되었는지 확인 (조회)
        Optional<User> foundUser2AfterDelete = userService.findById(user2.getUserId());
        System.out.println("[조회] Bob 삭제 후 재조회 (존재 여부): " + foundUser2AfterDelete.isPresent());
        List<User> remainingUsers = userService.findAll();
        System.out.println("남아있는 사용자:");
        remainingUsers.forEach(System.out::println);

        System.out.println("\n--- 사용자(User) 서비스 테스트 종료 ---\n");


        System.out.println("--- 채널(Channel) 서비스 테스트 시작 ---");

        // 1. 채널 등록 (Create)
        Channel channel1 = new Channel("General", "일반적인 대화를 위한 채널");
        Channel channel2 = new Channel("Development", "개발 관련 논의 채널");
        channelService.create(channel1);
        channelService.create(channel2);
        System.out.println("\n[등록] 채널 2개 등록 완료.");

        // 2. 채널 조회 (단건)
        Optional<Channel> foundChannel1 = channelService.findById(channel1.getChannelId());
        foundChannel1.ifPresent(channel -> System.out.println("\n[조회] 단건 조회 (General): " + channel));

        // 3. 채널 조회 (다건)
        List<Channel> allChannels = channelService.findAll();
        System.out.println("\n[조회] 모든 채널 조회:");
        allChannels.forEach(System.out::println);

        // 4. 채널 수정 (Update)
        channel1.updateChannel("General Chat", "자유로운 대화를 위한 채널");
        Optional<Channel> updatedChannel1 = channelService.update(channel1.getChannelId(), channel1);
        updatedChannel1.ifPresent(channel -> System.out.println("\n[수정] General -> General Chat으로 수정 완료: " + channel));

        // 5. 수정된 데이터 조회 확인
        Optional<Channel> reFoundChannel1 = channelService.findById(channel1.getChannelId());
        reFoundChannel1.ifPresent(channel -> System.out.println("[조회] 수정 후 General Chat 재조회: " + channel));

        // 6. 채널 삭제 (Delete)
        boolean deletedChannel = channelService.delete(channel2.getChannelId());
        System.out.println("\n[삭제] Development 채널 삭제 결과: " + (deletedChannel ? "성공" : "실패"));

        // 7. 삭제되었는지 확인 (조회)
        Optional<Channel> foundChannel2AfterDelete = channelService.findById(channel2.getChannelId());
        System.out.println("[조회] Development 채널 삭제 후 재조회 (존재 여부): " + foundChannel2AfterDelete.isPresent());
        List<Channel> remainingChannels = channelService.findAll();
        System.out.println("남아있는 채널:");
        remainingChannels.forEach(System.out::println);

        System.out.println("\n--- 채널(Channel) 서비스 테스트 종료 ---\n");


        System.out.println("--- 메시지(Message) 서비스 테스트 시작 (심화 요구사항 포함) ---");

        // 1. 메시지 등록 (Create) - 유효한 사용자 및 채널
        Message msg1 = new Message("Hello everyone!", user1.getUserId(), channel1.getChannelId());
        Message msg2 = new Message("Hello everyone!", user2.getUserId(), channel2.getChannelId());
        Message msg3 = new Message("Hello everyone!", user3.getUserId(), channel2.getChannelId());
        messageService.create(msg1);
        messageService.create(msg2);
        messageService.create(msg3);
        System.out.println("\n[등록] 유효한 메시지 등록 완료. : " + msg1);

        // 1-1. 메시지 등록 (Create) - 존재하지 않는 사용자 ID로 시도 (검증 실패 예상)
        Message msgInvalidUser = new Message("This message should fail.", UUID.randomUUID(), channel1.getChannelId());
        System.out.println("\n[등록] 존재하지 않는 사용자 ID로 메시지 등록 시도 (실패 예상):");
        Message createdInvalidUserMsg = messageService.create(msgInvalidUser);
        System.out.println("생성 결과: " + (createdInvalidUserMsg != null ? "성공" : "실패 (예상대로)"));

        // 1-2. 메시지 등록 (Create) - 존재하지 않는 채널 ID로 시도 (검증 실패 예상)
        Message msgInvalidChannel = new Message("This message should also fail.", user1.getUserId(), UUID.randomUUID());
        System.out.println("\n[등록] 존재하지 않는 채널 ID로 메시지 등록 시도 (실패 예상):");
        Message createdInvalidChannelMsg = messageService.create(msgInvalidChannel);
        System.out.println("생성 결과: " + (createdInvalidChannelMsg != null ? "성공" : "실패 (예상대로)"));

        // 2. 메시지 조회 (단건)
        Optional<Message> foundMsg1 = messageService.findById(msg1.getMessageId());
        foundMsg1.ifPresent(message -> System.out.println("\n[조회] 단건 조회 (Hello everyone!): " + message));

        // 3. 메시지 조회 (다건)
        List<Message> allMessages = messageService.findAll();
        System.out.println("\n[조회] 모든 메시지 조회:");
        allMessages.forEach(System.out::println);

        // 4. 메시지 수정 (Update)
        // Message 객체 자체의 update 메서드를 호출하여 필드를 변경합니다.
        msg1.updateMessage("Hello, world! (updated)");
        // 서비스의 update 메서드를 호출하여 변경된 내용을 반영하고, 저장소에 업데이트합니다.
        Optional<Message> updatedMsg1 = messageService.update(msg1.getMessageId(), msg1);
        updatedMsg1.ifPresent(message -> System.out.println("\n[수정] 메시지 수정 완료: " + message));

        // 5. 수정된 데이터 조회 확인
        Optional<Message> reFoundMsg1 = messageService.findById(msg1.getMessageId());
        reFoundMsg1.ifPresent(message -> System.out.println("[조회] 수정 후 메시지 재조회: " + message));

        // 6. 메시지 삭제 (Delete)
        boolean deletedMsg = messageService.delete(msg1.getMessageId());
        System.out.println("\n[삭제] 메시지 삭제 결과: " + (deletedMsg ? "성공" : "실패"));

        // 7. 삭제되었는지 확인 (조회)
        Optional<Message> foundMsg1AfterDelete = messageService.findById(msg1.getMessageId());
        System.out.println("[조회] 메시지 삭제 후 재조회 (존재 여부): " + foundMsg1AfterDelete.isPresent());
        List<Message> remainingMessages = messageService.findAll();
        System.out.println("남아있는 메시지:");
        remainingMessages.forEach(System.out::println);

        System.out.println("\n--- 메시지(Message) 서비스 테스트 종료 ---");
    }
}
