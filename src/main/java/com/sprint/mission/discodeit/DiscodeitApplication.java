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
import java.time.temporal.ChronoUnit;
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

    static void onlineTest(User user) {
        Clock base = Clock.fixed(Instant.parse("2025-08-10T08:00:00Z"), ZoneOffset.UTC);
        UserStatus userStatus1 = new UserStatus(user.getId(), Instant.now(base).minusSeconds(299));
        UserStatus userStatus2 = new UserStatus(user.getId(), Instant.now(base).minusSeconds(301));
        System.out.println("299초 전 online : " + userStatus1.isOnline(Instant.now(base)));
        System.out.println("301초 전 online : " + userStatus2.isOnline(Instant.now(base)));
    }

    static void readStatusTest(ReadStatus readStatus, Message message) {
        System.out.println("안읽음 여부 : " + readStatus.isUnread(message.getCreatedAt()));
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService       = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        Instant base = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Clock past = Clock.fixed(base.minusSeconds(600), ZoneOffset.UTC); // 10분 전 기준시각 설정
        Clock future = Clock.fixed(base.plusSeconds(600), ZoneOffset.UTC); // 10분 후 기준시각 설정

        ReadStatus readStatus1 = new ReadStatus(user.getId(), channel.getId(), past);
        ReadStatus readStatus2 = new ReadStatus(user.getId(), channel.getId(), past);

        // 테스트
        System.out.println("------------- 메시지 생성 Test -------------");
        messageCreateTest(messageService, channel, user); // 메시지 생성 및 출력 테스트
        System.out.println();

        System.out.println("------------- Online Test -------------");
        onlineTest(user); // User online 여부 확인(299초 전, 301초 전)
        System.out.println();

        System.out.println("------------- ReadStatus-markRead 메서드 Test -------------");
        Message message1 = messageService.create("ReadTest1", channel.getId(), user.getId());
        readStatusTest(readStatus1, message1); // true(안읽음)
        readStatus1.markRead(message1.getCreatedAt()); // 읽음처리
        readStatusTest(readStatus1, message1); // false(읽음)
        System.out.println();

        System.out.println("------------- ReadStatus 시간별 Test -------------");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {} // 1초 대기, message1 생성 시각과 다르게 설정

        Message message2 = messageService.create("ReadTest2", channel.getId(), user.getId());
        readStatusTest(readStatus2, message2); // true(안읽음), 10분 전
        readStatus2.markRead(message2.getCreatedAt().plusSeconds(1), future);
        readStatusTest(readStatus2, message2); // false(읽음), 10분 후

        System.out.println("http://localhost:8080/");

    }

}
