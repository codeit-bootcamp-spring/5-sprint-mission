package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;

    public static void main(String[] args) {
        JavaApplication app = new JavaApplication();
        app.initializeServices();

        app.testUserCRUD();
        app.testChannelCRUD();
        app.testMessageCRUD();
    }

    private void initializeServices() {
/*
        this.userService = new JCFUserService();
        this.channelService = new JCFChannelService();
        this.messageService = new JCFMessageService();
*/

        this.userService = new FileUserService();
        this.channelService = new FileChannelService();
        this.messageService = new FileMessageService();

        System.out.println("초기화 완료");
    }

    private void testUserCRUD() {
        System.out.println("=== User CRUD 테스트 ===");

        System.out.println("=== User 등록 ===");
        User user1 = userService.create("홍길동", "1234");
        User user2 = userService.create("김길동", "4321");
        System.out.println(user1);
        System.out.println(user2);

        System.out.println("\n=== User 단건 조회 ===");
        User findUser = userService.find(user1.getId());
        System.out.println(user1.equals(findUser));

        System.out.println("\n=== User 다건 조회 ===");
        System.out.println("user 개수: " + userService.findAll().size());
        System.out.println(userService.findAll());

        System.out.println("\n=== User 수정 ===");
        User updateUser = userService.update(user2.getId(), "김김길동", "1234");

        System.out.println("\n=== 수정된 User 데이터 조회 ===");
        System.out.println(userService.find(updateUser.getId()));

        System.out.println("\n=== User 삭제 ===");
        userService.delete(user1.getId());
        userService.delete(user2.getId());

        System.out.println("\n=== User 삭제 확인 ===");
        System.out.println(userService.findAll());

        System.out.println("=== User CRUD 테스트 완료 ===");
        System.out.println();
    }

    private void testChannelCRUD() {
        System.out.println("=== Channel CRUD 테스트 ===");

        System.out.println("\n=== Channel 등록 ===");
        Channel channel1 = channelService.create("스프링 백엔드 5기");
        Channel channel2 = channelService.create("스프링 백엔드 6기");
        System.out.println(channel1);
        System.out.println(channel2);

        System.out.println("\n=== Channel 조회 ===");
        Channel findChannel = channelService.find(channel1.getId());
        System.out.println(channel1.equals(findChannel));

        System.out.println("\n=== Channel 다건 조회 ===");
        System.out.println("channel 개수: " + channelService.findAll().size());
        System.out.println(channelService.findAll());

        System.out.println("\n=== Channel 수정 ===");
        Channel updateChannel = channelService.update(channel1.getId(),"코드잇 스프링 백엔드 5기");

        System.out.println("\n=== Channel 수정된 데이터 조회 ===");
        System.out.println(channelService.find(updateChannel.getId()));

        System.out.println("\n=== Channel 삭제 ===");
        channelService.delete(channel2.getId());

        System.out.println("\n=== Channel 삭제 확인 ===");
        System.out.println(channelService.findAll());

        System.out.println("=== Channel CRUD 테스트 완료 ===");
        System.out.println();
    }

    private void testMessageCRUD() {
        System.out.println("=== Message CRUD 테스트 ===");

        System.out.println("\n=== Message 등록 ===");
        User user1 = userService.create("홍길동", "1234");
        User user2 = userService.create("김길동", "4321");
        Channel channel1 = channelService.create("스프링 백엔드 5기");
        Channel channel2 = channelService.create("스프링 백엔드 6기");
        Message message1 = messageService.create("안녕하세요.", channel1.getId(), user1.getId());
        Message message2 = messageService.create("반갑습니다.", channel2.getId(), user2.getId());
        System.out.println(message1);
        System.out.println(message2);

        System.out.println("\n=== Message 단건 조회 ===");
        Message findMessage = messageService.find(message1.getId());
        System.out.println(message1.equals(findMessage));

        System.out.println("\n=== Message 다건 조회 ===");
        System.out.println("message 개수 : " + messageService.findAll().size());
        System.out.println(messageService.findAll());

        System.out.println("\n=== Message 수정 ===");
        Message updateMessage = messageService.update(message1.getId(), "스프린트 미션 중", channel1.getId(), user1.getId());

        System.out.println("\n=== 수정된 Message 조회 ===");
        System.out.println(messageService.find(updateMessage.getId()));

        System.out.println("\n=== Message 삭제 ===");
        messageService.delete(message2.getId());

        System.out.println("\n=== Message 삭제 확인 ===");
        System.out.println(messageService.findAll());

        System.out.println("=== Message CRUD 테스트 완료 ===");
    }
}
