package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static byte[] loadResourceBytes(String filename) {
        List<String> candidates = List.of(
                filename,
                "static/" + filename,
                "images/" + filename
        );

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (String path : candidates) {
            try (InputStream in = cl.getResourceAsStream(path)) {
                if (in != null) {
                    System.out.println("리소스 로드 성공 (classpath): " + path);
                    return in.readAllBytes();
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        Path fsPath = Path.of(System.getProperty("user.dir"), filename);
        if (Files.exists(fsPath)) {
            try {
                System.out.println("리소스 로드 성공 (filesystem): " + fsPath.toAbsolutePath());
                return Files.readAllBytes(fsPath);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        throw new IllegalStateException(
                "이미지 리소스를 찾을 수 없습니다. 다음 위치 중 하나에 파일을 추가하세요:\n" +
                        "- src/main/resources/" + filename + "\n" +
                        "- src/main/resources/static/" + filename + "\n" +
                        "- src/main/resources/images/" + filename + "\n" +
                        "또는 작업 디렉터리(" + System.getProperty("user.dir") + ")에 " + filename + " 파일을 배치하세요."
        );
    }

    static void repositoryTest(BinaryContentRepository repository, UserResponseDto user, MessageResponseDto message) {
        final String filename = "images.jpg";
        byte[] bytes = loadResourceBytes(filename);

        UUID userId = user.id();
        UUID messageId = message.id();

        BinaryContent content = new BinaryContent(
                UUID.randomUUID(),
                userId,
                bytes,
                filename,
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
