package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);

        // 파일 테스트
        User fileUser = new User("김길동", "bbb@bb", "5678");
        FileUserRepository fileUserRepository = context.getBean(FileUserRepository.class);
        fileUserRepository.save(fileUser);
        System.out.println(fileUserRepository.findById(fileUser.getId()));

    }

    static User setupUser(UserService userService) {
        User user = userService.create("홍길동", "aaa@aa", "1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "Sports", "경기 기록 공유방");
        return channel;
    }

    private static void messageCreateTest(MessageService messageService, Channel channel, User user) {
        Message message = messageService.create("7/18 경기", channel.getId(), user.getId());
        System.out.println("메시지 생성 완료: " + message.getContent());
    }
}
