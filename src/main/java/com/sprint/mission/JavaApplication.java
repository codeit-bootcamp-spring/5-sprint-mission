package com.sprint.mission;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.respository.file.*;
import com.sprint.mission.discodeit.respository.jcf.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import com.sprint.mission.discodeit.service.file.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        System.out.println("----------------File*Service 테스트----------------");
        testFileService();
        System.out.println();

        System.out.println("----------------JCF*Service 테스트----------------");
        testJCFService();
        System.out.println();

        System.out.println("----------------Basic*Service + JCFRepository 테스트----------------");
        testBasicServiceWithJCFRepository();
        System.out.println();

        System.out.println("----------------Basic*Service + FileRepository 테스트----------------");
        testBasicServiceWithFileRepository();
    }

    public static void testFileService() {
        // Given
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        // When
        User user = userService.create("소연", "isylsy166@gmail.com", "1234");
        Channel channel = channelService.create("공지", ChannelType.TEXT);
        Message message = messageService.create(user, channel, "안녕하세요.");

        // Then
        System.out.println(user.toString());
        System.out.println(channel.toString());
        System.out.println(message.toString());
    }

    public static void testJCFService() {
        // Given
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        // When
        User user = userService.create("소연1", "soyeon1@gmail.com", "1234");
        Channel channel = channelService.create("공지", ChannelType.TEXT);
        Message message = messageService.create(user, channel, "안녕하세요.");

        // Then
        System.out.println(user.toString());
        System.out.println(channel.toString());
        System.out.println(message.toString());
    }

    // Basic + JCFRepository
    public static void testBasicServiceWithJCFRepository() {

        // Given
        UserService userService = new BasicUserService(new JCFUserRepository());
        ChannelService channelService = new BasicChannelService(new JCFChannelRepository());
        MessageService messageService = new BasicMessageService(new JCFMessageRepository());

        // When
        User user = userService.create("소연2", "soyeon2@gmail.com",  "1234");
        Channel channel = channelService.create("스터디모집", ChannelType.VOICE);
        Message message = messageService.create(user, channel, "안녕하세요.");

        // Then
        System.out.println(user.toString());
        System.out.println(channel.toString());
        System.out.println(message.toString());
    }

    // Basic + FileRepository
    public static void testBasicServiceWithFileRepository() {
        // Given
        UserService userService = new BasicUserService(new FileUserRepository());
        ChannelService channelService = new BasicChannelService(new FileChannelRepository());
        MessageService messageService = new BasicMessageService(new FileMessageRepository());

        // When: 데이터 생성
        User user = userService.create("소연3","soyeon3@gmail.com",  "1234");
        Channel channel = channelService.create("음성", ChannelType.TEXT);
        Message message = messageService.create(user, channel, "안녕하세요.");

        // Then: 생성 확인
        System.out.println(user.toString());
        System.out.println(channel.toString());
        System.out.println(message.toString());

        System.out.println("---------------- 삭제 테스트 ----------------");

        // Message 삭제 성공
        boolean messageDeleted = messageService.deleteById(message.getId());
        System.out.println("메시지 삭제 성공 여부: " + messageDeleted);

        // Message 삭제 실패
        boolean messageDeleteFail = messageService.deleteById(UUID.randomUUID());
        System.out.println("존재하지 않는 메시지 삭제 실패 여부 (false 기대): " + messageDeleteFail);

        // Channel 삭제 성공
        boolean channelDeleted = channelService.delete(channel.getId());
        System.out.println("채널 삭제 성공 여부: " + channelDeleted);

        // Channel 삭제 실패
        boolean channelDeleteFail = channelService.delete(UUID.randomUUID());
        System.out.println("존재하지 않는 채널 삭제 실패 여부 (false 기대): " + channelDeleteFail);

        // User 삭제 성공
        boolean userDeleted = userService.delete(user.getId());
        System.out.println("유저 삭제 성공 여부: " + userDeleted);

        // User 삭제 실패
        boolean userDeleteFail = userService.delete(UUID.randomUUID());
        System.out.println("존재하지 않는 유저 삭제 실패 여부 (false 기대): " + userDeleteFail);
    }

    private static User createTestUser(UserService userService, String name) {
        return userService.create(name, "soyeon4@gmail.com", "1234");
    }

    private static Channel createTestChannel(ChannelService channelService, String name, ChannelType type) {
        return channelService.create(name, type);
    }

    private static Message createTestMessage(MessageService messageService, User user, Channel channel, String content) {
        return messageService.create(user, channel, content);
    }
}