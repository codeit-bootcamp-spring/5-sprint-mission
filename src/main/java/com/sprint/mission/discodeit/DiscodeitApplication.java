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
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {

        // Spring Context 사용
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

     /*   // 리포지토리 테스트
        BinaryContentRepository repository = context.getBean("binaryContent",BinaryContentRepository.class);
        repositoryTest(repository);
*/
        // 서비스 초기화
        UserService userService = context.getBean("userService", UserService.class);
        ChannelService channelService = context.getBean("channelService",ChannelService.class);
        MessageService messageService = context.getBean("messageService",MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);

        // --- 파일 저장소 테스트 -------
        FileUserRepository fileUserRepository = new FileUserRepository();
        FileChannelRepository fileChannelRepository = new FileChannelRepository();
        FileMessageRepository fileMessageRepository = new FileMessageRepository();

        // 유저 저장 테스트
        User fileUser = new User("alice", "alice@gmail.com", "1234",null);
        fileUserRepository.save(fileUser);
        System.out.println("FileUserRepository 저장");
        System.out.println("저장한 유저 이름 : " + fileUserRepository.findById(fileUser.getId()).get().getUsername());

        // 채널 저장 테스트
        Channel fileChannel = new Channel(ChannelType.PUBLIC, "공지", "공지채널입니다");
        fileChannelRepository.save(fileChannel);
        System.out.println("FileChannelRepository 저장");
        System.out.println("저장한 채널 : " + fileChannelRepository.findById(fileChannel.getId()).get().getName());

        // 메세지 저장 테스트
        Message fileMessage = new Message("test message",fileChannel.getId(),fileUser.getId(),null);
        fileMessageRepository.save(fileMessage);
        System.out.println("FileMessageRepository 저장");
        System.out.println("저장한 메세지 : " + fileMessageRepository.findById(fileMessage.getId()).get().getContent());

    }


    static User setupUser(UserService userService) {
        User user = userService.create(new UserCreateRequest( UUID.randomUUID(),"woody", "woody@codeit.com", "1234",null));
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.createPublicChannel(new PublicChannelCreateRequest("woody", "woody 채널입니다."));
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(new MessageCreateRequest(author.getId(),channel.getId(),"Hello",null));
        System.out.println("메시지 생성: " + message.getContent());
    }

/*
    static void repositoryTest(BinaryContentRepository repository) {
        byte[] bytes;
        try {
            Path imagePath = Path.of(System.getProperty("user.dir"), "images.jpg");
            System.out.println(imagePath.toAbsolutePath());
            bytes = Files.readAllBytes(imagePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BinaryContent content = new BinaryContent("images.jpg","jpg",(long)bytes.length, bytes,null,UUID.randomUUID());

        BinaryContent savedContent = repository.save(content);

        System.out.println(savedContent.getId() + " : " + savedContent.getContentType() + " : " + savedContent.getSize());
        Optional<BinaryContent> savedContent2 =  repository.findById(savedContent.getId());
        System.out.println(savedContent2.isPresent()); // true?
    }
*/

}
