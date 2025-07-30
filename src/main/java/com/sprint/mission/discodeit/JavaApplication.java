package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
/*import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;*/
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;

    public static void main(String[] args) {
        // 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(channelService, userService);

/*        // 테스트
        testUserCRUD(userService);
        System.out.println();
        testChannelCRUD(channelService);
        System.out.println();
        testMessageCRUD(messageService);*/

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }

    private static void testUserCRUD(UserService userService) {
        System.out.println("=== User CRUD 테스트 ===");

        System.out.println("=== User 등록 ===");
        User user1 = userService.create("홍길동", "test1@email.com", "1234");
        User user2 = userService.create("김길동", "test2@email.com", "4321");
        System.out.println(user1);
        System.out.println(user2);

        System.out.println("\n=== User 단건 조회 ===");
        User findUser = userService.find(user1.getId());
        System.out.println(findUser);
        System.out.println("user1과 조회된 User가 동일한가? : " + user1.equals(findUser));

        System.out.println("\n=== User 다건 조회 ===");
        System.out.println("user 개수: " + userService.findAll().size());
        System.out.println(userService.findAll());

        System.out.println("\n=== User 수정 ===");
        User updateUser = userService.update(user2.getId(), "김김길동", "1234@email.com", "123456");

        System.out.println("\n=== 수정된 User 데이터 조회 ===");
        System.out.println(userService.find(updateUser.getId()));

        System.out.println("\n=== User 삭제 ===");
        userService.delete(user1.getId());
        userService.delete(user2.getId());

        System.out.println("\n=== User 삭제 확인 ===");
        System.out.println(userService.findAll());

        System.out.println("=== User CRUD 테스트 완료 ===");
    }

    private static void testChannelCRUD(ChannelService channelService) {
        System.out.println("=== Channel CRUD 테스트 ===");

        System.out.println("=== Channel 등록 ===");
        Channel channel1 = channelService.create(ChannelType.PUBLIC, "스프링 백엔드 5기", "5기");
        Channel channel2 = channelService.create(ChannelType.PRIVATE, "스프링 백엔드 6기", "6기");
        System.out.println(channel1);
        System.out.println(channel2);

        System.out.println("\n=== Channel 단건 조회 ===");
        Channel findChannel = channelService.find(channel1.getId());
        System.out.println(findChannel);
        System.out.println("channel1과 조회된 Channel가 동일한가? : " + channel1.equals(findChannel));

        System.out.println("\n=== Channel 다건 조회 ===");
        System.out.println("channel 개수: " + channelService.findAll().size());
        System.out.println(channelService.findAll());

        System.out.println("\n=== Channel 수정 ===");
        Channel updateChannel = channelService.update(channel1.getId(), "코드잇 스프링 백엔드 5기", null);

        System.out.println("\n=== Channel 수정된 데이터 조회 ===");
        System.out.println(channelService.find(updateChannel.getId()));

        System.out.println("\n=== Channel 삭제 ===");
        channelService.delete(channel2.getId());

        System.out.println("\n=== Channel 삭제 확인 ===");
        System.out.println(channelService.findAll());

        System.out.println("=== Channel CRUD 테스트 완료 ===");
    }

    private static void testMessageCRUD(MessageService messageService) {
        System.out.println("=== Message CRUD 테스트 ===");

        System.out.println("=== Message 등록 ===");
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Message message1 = messageService.create("안녕하세요.", channelId, authorId);
        Message message2 = messageService.create("반갑습니다.", channelId, authorId);
        System.out.println(message1);
        System.out.println(message2);

        System.out.println("\n=== Message 단건 조회 ===");
        Message findMessage = messageService.find(message1.getId());
        System.out.println(findMessage);
        System.out.println("message1과 조회된 Message가 동일한가? : " + message1.equals(findMessage));

        System.out.println("\n=== Message 다건 조회 ===");
        System.out.println("message 개수 : " + messageService.findAll().size());
        System.out.println(messageService.findAll());

        System.out.println("\n=== Message 수정 ===");
        Message updateMessage = messageService.update(message1.getId(), "스프린트 미션 중");

        System.out.println("\n=== 수정된 Message 조회 ===");
        System.out.println(messageService.find(updateMessage.getId()));

        System.out.println("\n=== Message 삭제 ===");
        messageService.delete(message2.getId());

        System.out.println("\n=== Message 삭제 확인 ===");
        System.out.println(messageService.findAll());

        System.out.println("=== Message CRUD 테스트 완료 ===");
    }

    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }
}
