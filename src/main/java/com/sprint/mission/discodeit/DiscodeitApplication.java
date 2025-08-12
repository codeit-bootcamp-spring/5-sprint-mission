package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {
    static User setupUser(UserService userService) {
        UserCreateRequest request = new UserCreateRequest("woody", "woody@codeit.com", "woody1234");
        User user = userService.create(request,Optional.empty());
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지 채널입니다.");
        Channel channel = channelService.create(request);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        MessageCreateRequest request = new MessageCreateRequest("안녕하세요.", channel.getId(), author.getId());
        Message message = messageService.create(request);
        System.out.println("메시지 생성: " + message.getId());
    }

    static void repositoryTest(BinaryContentRepository repository) {
        byte[] bytes;
        try {
            Path imagePath = Path.of(System.getProperty("user.dir"), "images.jpg");
            System.out.println(imagePath.toAbsolutePath());
            bytes = Files.readAllBytes(imagePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // String fileName, String contentType, Long size, byte[] bytes
        BinaryContent content = new BinaryContent("images.jpg","jpg",(long)bytes.length, bytes);

        BinaryContent savedContent = repository.save(content);

        System.out.println(savedContent.getId() + " : " + savedContent.getContentType() + " : " + savedContent.getSize());
        Optional<BinaryContent> savedContent2 =  repository.findById(savedContent.getId());
        System.out.println(savedContent2.isPresent()); // true?
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 리포지토리 테스트
        BinaryContentRepository repository = context.getBean(BinaryContentRepository.class);
        repositoryTest(repository);


        // 서비스 초기화
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}
