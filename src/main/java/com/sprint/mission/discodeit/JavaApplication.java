package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JavaApplication {
    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;

    public static void main(String[] args) {
        JavaApplication app = new JavaApplication();
        app.initializeServices();

        app.testCreate();
//        app.testFind();
//        app.testDataIntegrity();
//        app.testConcurrentAccess();
//        app.testFullScenario();
    }

    private void initializeServices() {
        this.userService = new JCFUserService();
        this.channelService = new JCFChannelService();
        this.messageService = new JCFMessageService();

        System.out.println("서비스 초기화 완료");
    }

    private void testCreate() {
        System.out.println("=== 등록 테스트 ===");

        User user1 = userService.create("홍길동", "1234");
        User user2 = userService.create("김길동", "4321");
        System.out.println("등록된 사용자");
        System.out.println(user1);
        System.out.println(user2);

        System.out.println("------------------------- 사용자 단건 조회 -------------------------");
        User findUser = userService.find(user1.getId());
        System.out.println(user1.equals(findUser));

        System.out.println("사용자 다건 조회");
        System.out.println(userService.findAll().size());
        System.out.println(userService.findAll());

        System.out.println("사용자 수정");
        User updateUser = userService.find(user2.getId());
        updateUser.update("김김길동", "1234");
        updateUser.update("김김길동", "4321");

        System.out.println("사용자 수정된 데이터 조회");
        System.out.println(userService.find(updateUser.getId()));

        System.out.println("사용자 삭제");
        userService.delete(user1.getId());
        userService.delete(user2.getId());
        System.out.println(userService.findAll());

        System.out.println("사용자 조회를 통해 삭제되었는지 확인");
        // 이렇게 작성하는게 맞는지 확인
        try {
            User findUser2 = userService.find(user1.getId());
            System.out.println(findUser2);
        } catch (Exception e) {
            System.out.println("해당 유저 없음");
        }

        Channel channel1 = channelService.create("스프링 백엔드 5기");
        Channel channel2 = channelService.create("스프링 백엔드 6기");
        System.out.println("등록된 채널");
        System.out.println(channel1);
        System.out.println(channel2);

        System.out.println("채널 조회");
        Channel findChannel = channelService.find(channel1.getId());
        System.out.println(channel1.equals(findChannel));

        System.out.println("채널 다건 조회");
        System.out.println(channelService.findAll().size());
        System.out.println(channelService.findAll());

        System.out.println("채널 수정");
        channel1.update("코드잇 스프링 백엔드 5기");
        System.out.println(channel1);

        System.out.println("채널 수정된 데이터 조회");
        System.out.println(channelService.find(channel1.getId()));

        System.out.println("채널 삭제");
        channelService.delete(channel2.getId());
        System.out.println(channelService.findAll());

        System.out.println("채널 조회를 통해 삭제되었는지 확인");


        Message message1 = messageService.create("안녕하세요.", channel1.getId(), user1.getId());
        Message message2 = messageService.create("반갑습니다.", channel2.getId(), user2.getId());
        System.out.println("등록된 메세지");
        System.out.println(message1);
        System.out.println(message2);


        System.out.println("메세지 조회");
        Message findMessage = messageService.find(message1.getId());
        System.out.println(findMessage);
        System.out.println(message1.equals(findMessage));

        System.out.println("메세지 수정");
        message1.update("스프린트 미션 중", channel1.getId(), user1.getId());
        System.out.println(message1);

        System.out.println("수정된 메세지 조회");
        System.out.println(messageService.find(message1.getId()));

        System.out.println("메세지 삭제");
        messageService.delete(message2.getId());
        System.out.println(messageService.findAll());
        System.out.println("삭제 확인");

        System.out.println("등록 테스트 완료");
    }

    private void testFind() {
//        System.out.println("\n=== 조회 테스트 ===");
//
//        User testUser = userService.createUser(new User(System.currentTimeMillis()));
//        Channel testChannel = channelService.createChannel(new Channel(System.currentTimeMillis()));
//        Message testMessage = messageService.createMessage(new Message(System.currentTimeMillis()));
//
//        System.out.println("테스트 데이터 생성 완료");
//
//        System.out.println("\n단건 조회 테스트:");
//        User foundUser = userService.getUserById(testUser.getId());
//        Channel foundChannel = channelService.getChannelById(testChannel.getId());
//        Message foundMessage = messageService.getMessageById(testMessage.getId());
//
//        System.out.println("사용자 단건 조회: " + (foundUser != null ? "성공" : "실패"));
//        System.out.println("채널 단건 조회: " + (foundChannel != null ? "성공" : "실패"));
//        System.out.println("메시지 단건 조회: " + (foundMessage != null ? "성공" : "실패"));
//
//
//        System.out.println("\n다건 조회 테스트:");
//        List<User> allUsers = userService.findAll();
//        List<Channel> allChannels = channelService.getAllChannels();
//        List<Message> allMessages = messageService.getAllMessages();
//
//        System.out.println("전체 사용자 조회: " + allUsers.size() + "명");
//        System.out.println("전체 채널 조회: " + allChannels.size() + "개");
//        System.out.println("전체 메시지 조회: " + allMessages.size() + "개");
//
//        System.out.println("\n존재하지 않는 ID 조회 테스트:");
//        UUID nonExistentId = UUID.randomUUID();
//        User notFoundUser = userService.getUserById(nonExistentId);
//        Channel notFoundChannel = channelService.getChannelById(nonExistentId);
//        Message notFoundMessage = messageService.getMessageById(nonExistentId);
//
//        System.out.println("존재하지 않는 사용자 조회: " + (notFoundUser == null ? "올바른 null 반환" : "오류"));
//        System.out.println("존재하지 않는 채널 조회: " + (notFoundChannel == null ? "올바른 null 반환" : "오류"));
//        System.out.println("존재하지 않는 메시지 조회: " + (notFoundMessage == null ? "올바른 null 반환" : "오류"));
//
//        System.out.println("조회 기능 테스트 완료");
    }
//
//    private void testDataIntegrity() {
//        System.out.println("\n=== 수정 테스트 ===");
//
//        User userToUpdate = userService.createUser(new User(System.currentTimeMillis()));
//        Channel channelToUpdate = channelService.createChannel(new Channel(System.currentTimeMillis()));
//        Message messageToUpdate = messageService.createMessage(new Message(System.currentTimeMillis()));
//
//        System.out.println("수정할 테스트 데이터 생성 완료");
//
//        long originalUserCreateTime = userToUpdate.getCreateAt();
//        long originalChannelCreateTime = channelToUpdate.getCreateAt();
//        long originalMessageCreateTime = messageToUpdate.getCreateAt();
//
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        System.out.println("\n수정 기능 테스트:");
//        userToUpdate.setUpdateAt(System.currentTimeMillis());
//        User updatedUser = userService.updateUser(userToUpdate);
//        System.out.println("사용자 수정: " + (updatedUser != null ? "성공" : "실패"));
//        if (updatedUser != null) {
//            System.out.println("  생성 시간 유지: " + (updatedUser.getCreateAt() == originalUserCreateTime ? "성공" : "실패"));
//            System.out.println("  수정 시간 갱신: " + (updatedUser.getUpdateAt() > originalUserCreateTime ? "성공" : "실패"));
//        }
//
//        channelToUpdate.setUpdateAt(System.currentTimeMillis());
//        Channel updatedChannel = channelService.updateChannel(channelToUpdate);
//        System.out.println("채널 수정: " + (updatedChannel != null ? "성공" : "실패"));
//        if (updatedChannel != null) {
//            System.out.println("  생성 시간 유지: " + (updatedChannel.getCreateAt() == originalChannelCreateTime ? "성공" : "실패"));
//            System.out.println("  수정 시간 갱신: " + (updatedChannel.getUpdateAt() > originalChannelCreateTime ? "성공" : "실패"));
//        }
//
//        messageToUpdate.setUpdateAt(System.currentTimeMillis());
//        Message updatedMessage = messageService.updateMessage(messageToUpdate);
//        System.out.println("메시지 수정: " + (updatedMessage != null ? "성공" : "실패"));
//        if (updatedMessage != null) {
//            System.out.println("  생성 시간 유지: " + (updatedMessage.getCreateAt() == originalMessageCreateTime ? "성공" : "실패"));
//            System.out.println("  수정 시간 갱신: " + (updatedMessage.getUpdateAt() > originalMessageCreateTime ? "성공" : "실패"));
//        }
//
//        System.out.println("\n존재하지 않는 엔티티 수정 테스트:");
//        User nonExistentUser = new User(UUID.randomUUID(), System.currentTimeMillis(), System.currentTimeMillis());
//        User notUpdatedUser = userService.updateUser(nonExistentUser);
//        System.out.println("존재하지 않는 사용자 수정: " + (notUpdatedUser == null ? "올바른 null 반환" : "오류"));
//
//        System.out.println("수정 기능 테스트 완료");
//    }
//
//    private void testConcurrentAccess() {
//        System.out.println("\n=== 삭제 테스트 ===");
//
//        User userToDelete = userService.createUser(new User(System.currentTimeMillis()));
//        Channel channelToDelete = channelService.createChannel(new Channel(System.currentTimeMillis()));
//        Message messageToDelete = messageService.createMessage(new Message(System.currentTimeMillis()));
//
//        System.out.println("삭제할 테스트 데이터 생성 완료");
//        System.out.println("삭제 대상 사용자 ID: " + userToDelete.getId());
//        System.out.println("삭제 대상 채널 ID: " + channelToDelete.getId());
//        System.out.println("삭제 대상 메시지 ID: " + messageToDelete.getId());
//
//        System.out.println("\n삭제 전 존재 확인:");
//        System.out.println("사용자 존재: " + (userService.getUserById(userToDelete.getId()) != null ? "확인" : "없음"));
//        System.out.println("채널 존재: " + (channelService.getChannelById(channelToDelete.getId()) != null ? "확인" : "없음"));
//        System.out.println("메시지 존재: " + (messageService.getMessageById(messageToDelete.getId()) != null ? "확인" : "없음"));
//
//        System.out.println("\n삭제 실행:");
//        userService.deleteUser(userToDelete.getId());
//        channelService.deleteChannel(channelToDelete.getId());
//        messageService.deleteMessage(messageToDelete.getId());
//        System.out.println("삭제 명령 실행 완료");
//
//        System.out.println("\n삭제 후 존재 확인:");
//        User deletedUser = userService.getUserById(userToDelete.getId());
//        Channel deletedChannel = channelService.getChannelById(channelToDelete.getId());
//        Message deletedMessage = messageService.getMessageById(messageToDelete.getId());
//
//        System.out.println("사용자 삭제: " + (deletedUser == null ? "성공" : "실패"));
//        System.out.println("채널 삭제: " + (deletedChannel == null ? "성공" : "실패"));
//        System.out.println("메시지 삭제: " + (deletedMessage == null ? "성공" : "실패"));
//
//        System.out.println("\n존재하지 않는 ID 삭제 테스트:");
//        UUID nonExistentId = UUID.randomUUID();
//        try {
//            userService.deleteUser(nonExistentId);
//            channelService.deleteChannel(nonExistentId);
//            messageService.deleteMessage(nonExistentId);
//            System.out.println("존재하지 않는 ID 삭제: 예외 없이 정상 처리");
//        } catch (Exception e) {
//            System.out.println("존재하지 않는 ID 삭제: 예외 발생 - " + e.getMessage());
//        }
//
//        System.out.println("삭제 기능 테스트 완료");
//    }
//
//    private void testFullScenario() {
//        System.out.println("\n=== 조회를 통해 삭제되었는지 확인 ===");
//
//        System.out.println("전체 시나리오 테스트 시작");
//
//        User scenarioUser = userService.createUser(new User(System.currentTimeMillis()));
//        Channel scenarioChannel = channelService.createChannel(new Channel(System.currentTimeMillis()));
//        Message scenarioMessage = messageService.createMessage(new Message(System.currentTimeMillis()));
//
//        UUID userId = scenarioUser.getId();
//        UUID channelId = scenarioChannel.getId();
//        UUID messageId = scenarioMessage.getId();
//
//        System.out.println("1. 생성 완료 사용자: " + userId + ", 채널: " + channelId + ", 메시지: " + messageId);
//
//        System.out.println("\n2. 생성 후 조회 확인:");
//        User foundUser = userService.getUserById(userId);
//        Channel foundChannel = channelService.getChannelById(channelId);
//        Message foundMessage = messageService.getMessageById(messageId);
//
//        System.out.println("사용자 조회: " + (foundUser != null ? "존재" : "없음"));
//        System.out.println("채널 조회: " + (foundChannel != null ? "존재" : "없음"));
//        System.out.println("메시지 조회: " + (foundMessage != null ? "존재" : "없음"));
//
//        System.out.println("\n3. 수정 테스트:");
//        if (foundUser != null) {
//            foundUser.setUpdateAt(System.currentTimeMillis());
//            userService.updateUser(foundUser);
//            System.out.println("사용자 수정 완료");
//        }
//
//        if (foundChannel != null) {
//            foundChannel.setUpdateAt(System.currentTimeMillis());
//            channelService.updateChannel(foundChannel);
//            System.out.println("채널 수정 완료");
//        }
//
//        if (foundMessage != null) {
//            foundMessage.setUpdateAt(System.currentTimeMillis());
//            messageService.updateMessage(foundMessage);
//            System.out.println("메시지 수정 완료");
//        }
//
//        System.out.println("\n4. 삭제 실행:");
//        userService.deleteUser(userId);
//        channelService.deleteChannel(channelId);
//        messageService.deleteMessage(messageId);
//        System.out.println("모든 엔티티 삭제 실행 완료");
//
//        System.out.println("\n5. 삭제 후 조회를 통한 확인:");
//        User deletedUser = userService.getUserById(userId);
//        Channel deletedChannel = channelService.getChannelById(channelId);
//        Message deletedMessage = messageService.getMessageById(messageId);
//
//        System.out.println("사용자 삭제 확인: " + (deletedUser == null ? "성공 (null 반환)" : "실패 (여전히 존재)"));
//        System.out.println("채널 삭제 확인: " + (deletedChannel == null ? "성공 (null 반환)" : "실패 (여전히 존재)"));
//        System.out.println("메시지 삭제 확인: " + (deletedMessage == null ? "성공 (null 반환)" : "실패 (여전히 존재)"));
//
//        System.out.println("\n6. 전체 목록 조회를 통한 최종 확인:");
//        List<User> finalUsers = userService.findAll();
//        List<Channel> finalChannels = channelService.getAllChannels();
//        List<Message> finalMessages = messageService.getAllMessages();
//
//        System.out.println("- 시스템 내 전체 사용자: " + finalUsers.size() + "명");
//        System.out.println("- 시스템 내 전체 채널: " + finalChannels.size() + "개");
//        System.out.println("- 시스템 내 전체 메시지: " + finalMessages.size() + "개");
//
//        boolean userNotInList = finalUsers.stream().noneMatch(u -> u.getId().equals(userId));
//        boolean channelNotInList = finalChannels.stream().noneMatch(c -> c.getId().equals(channelId));
//        boolean messageNotInList = finalMessages.stream().noneMatch(m -> m.getId().equals(messageId));
//
//        System.out.println("- 삭제된 사용자가 전체 목록에 없음: " + (userNotInList ? "확인" : "오류"));
//        System.out.println("- 삭제된 채널이 전체 목록에 없음: " + (channelNotInList ? "확인" : "오류"));
//        System.out.println("- 삭제된 메시지가 전체 목록에 없음: " + (messageNotInList ? "확인" : "오류"));
//
//        System.out.println("조회를 통한 삭제 확인 테스트 완료");
//        System.out.println("전체 시나리오 테스트 완료");
//    }
}
