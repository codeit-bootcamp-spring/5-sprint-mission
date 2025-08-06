package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@SpringBootApplication

public class DiscodeitApplication {
    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {

        Channel channel = channelService.createPublic(new ChannelCreateRequest(ChannelType.PUBLIC, "공지", "공지 채널입니다."));
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(new MessageCreateRequest("안녕하세요.", channel.getId(), author.getId()));
        System.out.println("메시지 생성: " + message.getId());
    }

    static void repositoryTest(BinaryContentRepository repository){
        // active에 따라 정상적으로 동작
        System.out.println("repository 종류 : " + repository.getClass().getSimpleName());


        byte[] bytes;
        try{
            Path imagePath=Path.of(System.getProperty("user.dir"),"images.jpg");
            System.out.println(imagePath.toAbsolutePath());
            bytes= Files.readAllBytes(imagePath);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BinaryContent content = new BinaryContent("images.jpg","jpg", (long)bytes.length, bytes );
        BinaryContent savedContent = repository.save(content);
        System.out.println(savedContent.getId()+ " : "+savedContent.getContentType()+ " : "+savedContent.getSize());
        Optional<BinaryContent> savedContent2 = repository.findById(savedContent.getId());



    }




    public static void main(String[] args) {

//        SpringApplication.run(DiscodeitApplication.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        //
        BinaryContentRepository repository = (BinaryContentRepository)context.getBean(BinaryContentRepository.class);
        repositoryTest(repository);



        // 서비스 초기화
        // TODO context에서 Bean을 조회하여 각 서비스 구현체 할당 코드 작성하세요.
        UserService userService = (UserService)context.getBean(UserService.class);

        ChannelService channelService = (ChannelService)context.getBean(ChannelService.class);

        MessageService messageService = (MessageService)context.getBean(MessageService.class);
        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService,channel, user);



    }

}
