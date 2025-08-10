package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    static UserResponseDto setupUser(UserService userService) {
        String username = "woody";
        String email = "woody@codeit.com";
        Optional<UserResponseDto> existing = userService.findAll().stream()
                .filter(u -> email.equals(u.email()) || username.equals(u.username()))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        }

        UserCreateRequest request = new UserCreateRequest(username, email, "woody1234", null);
        return userService.create(request);
    }

    static ChannelResponseDto setupChannel(ChannelService channelService, UserResponseDto user) {
        List<UUID> userIds = List.of(user.id());
        // PRIVATE 채널은 PrivateChannelCreateRequest로 생성
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                ChannelType.PRIVATE,
                userIds
        );
        return channelService.create(request);
    }

    static MessageResponseDto messageCreateTest(MessageService messageService, ChannelResponseDto channel, UserResponseDto author) {
        MessageCreateRequest request = new MessageCreateRequest(
                "안녕하세요.",
                channel.id(),
                author.id(),
                null
        );
        MessageResponseDto response = messageService.create(request);
        System.out.println("메시지 생성: " + response.id());
        return response;
    }

    static void repositoryTest(BinaryContentRepository repository, UserResponseDto user, MessageResponseDto message) {
        byte[] bytes;
        try {
            Path imagePath = Path.of(System.getProperty("user.dir"), "images.jpg");
            System.out.println("이미지 경로: " + imagePath.toAbsolutePath());
            bytes = Files.readAllBytes(imagePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UUID userId = user.id();
        UUID messageId = message.id();

        BinaryContent content = new BinaryContent(
                UUID.randomUUID(),
                userId,
                bytes,
                "images.jpg",
                "jpg",
                (long) bytes.length,
                messageId
        );

        BinaryContent savedContent = repository.save(content);

        System.out.println("저장된 컨텐츠 ID: " + savedContent.getId());
        System.out.println("컨텐츠 타입: " + savedContent.getContentType());
        System.out.println("컨텐츠 크기: " + savedContent.getSize());

        Optional<BinaryContent> retrieved = repository.findById(savedContent.getId());
        System.out.println("저장된 컨텐츠 조회 성공 여부: " + retrieved.isPresent());
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        UserResponseDto user = setupUser(userService);
        ChannelResponseDto channel = setupChannel(channelService, user);

        MessageResponseDto message = messageCreateTest(messageService, channel, user);

        BinaryContentRepository repository = context.getBean(BinaryContentRepository.class);
        repositoryTest(repository, user, message);
    }
}
