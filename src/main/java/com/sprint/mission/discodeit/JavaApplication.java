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
import java.util.NoSuchElementException;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        try {
            testUserService(userService);
            testChannelService(channelService, userService);
            testMessageService(messageService, userService, channelService);
        }catch (IllegalArgumentException | NoSuchElementException e){
            System.out.println(e.getMessage());
        }
    }

    private static void testUserService(UserService userService) {
        System.out.println("User 테스트");

        User user1 = userService.create("user1", "홍길동", "1234");
        User user2 = userService.create("user2", "김철수", "abcd");

        System.out.println("전체 유저 목록:");

        userService.getAll().forEach(user -> {
            System.out.println("Id: " + user.getId() + " 이름: " + user.getName());
        });

        System.out.println("user1 이름 변경  홍길동 -> 박길동");
        userService.updateUserName("user1", "박길동");

        System.out.println("비밀번호 변경: user2");
        userService.updatePassword("user2", "abcd", "새비밀번호");

        System.out.println("user2 삭제");
        userService.delete("user2");

        System.out.println("수정된 유저 목록:");
        userService.getAll().forEach(user -> {
            System.out.println("- " + user.getUserId() + ": " + user.getName());
        });
    }

    private static void testChannelService(ChannelService channelService, UserService userService) {
        System.out.println("Channel 테스트");
        System.out.println();

        User user3 = userService.create("test", "홍길동", "1234");

        Channel ch1 = channelService.create("일반채널", "기본 채팅방");
        Channel ch2 = channelService.create("공지채널", "공지용");

        System.out.println("전체 채널 목록:");
        channelService.getAll().forEach(c -> System.out.println("- " + c.getName() + ": " + c.getDescription()));

        System.out.println("채널 이름 수정: 일반채널 → 문의채널");
        channelService.updateName(ch1.getId(), "문의채널");

        System.out.println("채널 설명 수정: 문의채널");
        channelService.updateDescription(ch1.getId(), "문의용");

        System.out.println("공지채널 삭제");
        channelService.delete(ch2.getId());

        System.out.println("채널 목록:");
        channelService.getAll().forEach(c -> System.out.println("- " + c.getName() + ": " + c.getDescription()));
    }

    private static void testMessageService(MessageService messageService, UserService userService,
                                           ChannelService channelService) {
        System.out.println("Message 테스트");
        System.out.println();

        User user = userService.create("test1", "홍길동", "1234");
        Channel channel = channelService.create("일반", "일반 채팅 채널입니다");

        Message msg1 = messageService.create("안녕하세요", user.getUserId(), channel.getId());
        Message msg2 = messageService.create("수고하세요", user.getUserId(), channel.getId());

        System.out.println("전체 메시지:");
        messageService.getMessages().forEach(m -> System.out.println("- " + m.getContent()));

        System.out.println("메시지 수정: 안녕하세요 → 반갑습니다");
        messageService.update(msg1.getId(), "반갑습니다");

        System.out.println("메시지 삭제: 수고하세요");
        messageService.delete(msg2.getId());

        System.out.println("채널별 메시지:");
        messageService.getMessagesByChannel(channel.getId()).forEach(m -> System.out.println("- " + m.getContent()));

        System.out.println("유저별 메시지:");
        messageService.getMessagesByUser(user.getUserId()).forEach(m -> System.out.println("- " + m.getUserId() + m.getContent()));
    }
}
