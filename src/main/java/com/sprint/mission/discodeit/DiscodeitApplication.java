package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.respository.file.*;
import com.sprint.mission.discodeit.respository.jcf.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import com.sprint.mission.discodeit.service.file.*;
import com.sprint.mission.discodeit.service.jcf.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Override
    public void run(String... args) {
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

    private void testFileService() {
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        User user = userService.create("소연", "isylsy166@gmail.com", "1234");
        Channel channel = channelService.create("공지", ChannelType.TEXT);
        Message message = messageService.create(user, channel, "안녕하세요.");

        System.out.println(user);
        System.out.println(channel);
        System.out.println(message);
    }

    private void testJCFService() {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        User user = userService.create("소연1", "soyeon1@gmail.com", "1234");
        Channel channel = channelService.create("공지", ChannelType.TEXT);
        Message message = messageService.create(user, channel, "안녕하세요.");

        System.out.println(user);
        System.out.println(channel);
        System.out.println(message);
    }

    private void testBasicServiceWithJCFRepository() {
        UserService userService = new BasicUserService(new JCFUserRepository());
        ChannelService channelService = new BasicChannelService(new JCFChannelRepository());
        MessageService messageService = new BasicMessageService(new JCFMessageRepository());

        User user = userService.create("소연2", "soyeon2@gmail.com",  "1234");
        Channel channel = channelService.create("스터디모집", ChannelType.VOICE);
        Message message = messageService.create(user, channel, "안녕하세요.");

        System.out.println(user);
        System.out.println(channel);
        System.out.println(message);
    }

    private void testBasicServiceWithFileRepository() {
        UserService userService = new BasicUserService(new FileUserRepository());
        ChannelService channelService = new BasicChannelService(new FileChannelRepository());
        MessageService messageService = new BasicMessageService(new FileMessageRepository());

        User user = userService.create("소연3","soyeon3@gmail.com",  "1234");
        Channel channel = channelService.create("음성", ChannelType.TEXT);
        Message message = messageService.create(user, channel, "안녕하세요.");

        System.out.println(user);
        System.out.println(channel);
        System.out.println(message);

        System.out.println("---------------- 삭제 테스트 ----------------");

        boolean messageDeleted = messageService.deleteById(message.getId());
        System.out.println("메시지 삭제 성공 여부: " + messageDeleted);

        boolean messageDeleteFail = messageService.deleteById(UUID.randomUUID());
        System.out.println("존재하지 않는 메시지 삭제 실패 여부 (false 기대): " + messageDeleteFail);

        boolean channelDeleted = channelService.delete(channel.getId());
        System.out.println("채널 삭제 성공 여부: " + channelDeleted);

        boolean channelDeleteFail = channelService.delete(UUID.randomUUID());
        System.out.println("존재하지 않는 채널 삭제 실패 여부 (false 기대): " + channelDeleteFail);

        boolean userDeleted = userService.delete(user.getId());
        System.out.println("유저 삭제 성공 여부: " + userDeleted);

        boolean userDeleteFail = userService.delete(UUID.randomUUID());
        System.out.println("존재하지 않는 유저 삭제 실패 여부 (false 기대): " + userDeleteFail);
    }
}
