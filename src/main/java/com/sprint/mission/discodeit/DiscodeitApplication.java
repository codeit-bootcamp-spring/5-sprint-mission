package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    static User setupUser(UserService userService) {
        User user = userService.create("Hong", "hong1357@gmail.com", "hong1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하십니까?", channel.getId(), author.getId());
        System.out.println("메시지 ID : " + message.getId() + "\n" + message.getContent() + " by " + author.getUsername()
        + "\nWrited on " + message.getCreatedAt());
    }

    static boolean onlineWithin5Minutes_isTrue(User user) {
        Clock base = Clock.fixed(Instant.parse("2025-08-10T08:00:00Z"), ZoneOffset.UTC);
        UserStatus userStatus = new UserStatus(user.getId(), Instant.now(base).minusSeconds(299));
        return userStatus.isOnline(Instant.now(base));
    }

    static boolean onlineWithin5Minutes_isFalse(User user) {
        Clock base = Clock.fixed(Instant.parse("2025-08-10T08:00:00Z"), ZoneOffset.UTC);
        UserStatus userStatus = new UserStatus(user.getId(), Instant.now(base).minusSeconds(301));
        return !userStatus.isOnline(Instant.now(base));
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService       = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        // 테스트
        messageCreateTest(messageService, channel, user); // 메시지 테스트
        System.out.println(onlineWithin5Minutes_isTrue(user));
        System.out.println(onlineWithin5Minutes_isFalse(user));


        System.out.println("http://localhost:8080/");

    }

}
