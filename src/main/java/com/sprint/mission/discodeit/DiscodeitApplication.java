package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ReadStatusService readStatusService;
    private final UserStatusService userStatusService;
	private final BinaryContentService binaryContentService;

    @Autowired
    public DiscodeitApplication(UserService userService, ChannelService channelService, MessageService messageService
        , UserStatusService userStatusService, ReadStatusService readStatusService, BinaryContentService binaryContentService) {

        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.userStatusService = userStatusService;
        this.readStatusService = readStatusService;
		this.binaryContentService = binaryContentService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, new String[0]);
    }

    @Override
    public void run(String... args) {
        testUserService();
        testChannelService();
        testMessageService();
    }


    private UserDto.DetailResponse setupUser(UserService userService) {
        return userService.create(
            UserDto.CreateRequest.builder()
                .name("test")
                .email("test@test.com")
                .password("test1234")
                .build());
    }

    private ChannelDto.DetailResponse setupChannel(ChannelService channelService, UserDto.DetailResponse user) {

        return channelService.create(
            ChannelDto.CreateRequest.builder()
                .type(ChannelType.PUBLIC)
                .name("Test Channel")
                .description("Test Channel Description")
                .adminUserId(user.getId())
                .userId(null)
                .build());
    }

    private void messageCreateTest(MessageService messageService, ChannelDto.DetailResponse channel, UserDto.DetailResponse author) {
        MessageDto.DetailResponse message = messageService.create(
            MessageDto.CreateRequest.builder()
                .channelId(channel.getId())
                .authorId(author.getId())
                .text("안녕하세용")
                .build()
        );
        System.out.println("메시지 생성: " + message);
    }

    private void clear() {
        messageService.deleteAll();
        channelService.deleteAll();
        userService.deleteAll();
        readStatusService.deleteAll();
    }

    public void testUserService() {

        clear();
        System.out.println("============= 유저 테스트 시작 =============");

        UserDto.DetailResponse user = setupUser(userService);

        System.out.println("User 목록 : " + userService.findAll());

        UserDto.DetailResponse target = userService.findById(user.getId());
        System.out.println("유저 ID로 찾기 : " + target);
        if (target != null) {
            System.out.println("Test Target 유저 변경 : " + userService.update(UserDto.UpdateRequest.builder()
                .id(target.getId())
                .name("Update Test Target")
                .build()));
        }

        System.out.println("User 목록 : " + userService.findAll());

        System.out.println("Update Target User 유저 삭제");
        userService.delete(target.getId());
        System.out.println("채널 목록 : " + userService.findAll());

        userService.deleteAll();

        System.out.println("============= 유저 테스트 끝 =============");

    }

    public void testChannelService() {

        clear();
        System.out.println("============= 채널 테스트 시작 =============");

        UserDto.DetailResponse user = setupUser(userService);

        ChannelDto.DetailResponse testChannel = setupChannel(channelService, user);

        System.out.println("채널 목록 : " + channelService.findAll());
        System.out.println("User status :" + userStatusService.findAll());

        ChannelDto.DetailResponse target = channelService.findById(testChannel.getId());
        System.out.println("채널 ID로 찾기 : " + target);
        if (target != null) {
            System.out.println("Test Target 채널 변경 : " + channelService.update(
                ChannelDto.UpdateRequest.builder()
                    .id(target.getId())
                    .name("Update Test Target")
                    .description("Update Test Target Description")
                    .build()));
        }
        System.out.println("채널 목록 : " + channelService.findAll());

        System.out.println("채널에 유저 추가");
        System.out.println("채널 목록 : " + channelService.findAll());

        System.out.println("Test Target 채널 삭제");
        channelService.delete(target.getId());
        System.out.println("채널 목록 : " + channelService.findAll());

        channelService.deleteAll();
        System.out.println("============= 채널 테스트 끝 =============");
    }

    public void testMessageService() {

        clear();
        System.out.println("============= 메세지 테스트 시작 =============");

        UserDto.DetailResponse testUser1 = setupUser(userService);
        ChannelDto.DetailResponse testChannel = setupChannel(channelService, testUser1);


        MessageDto.DetailResponse message1 = messageService.create(MessageDto.CreateRequest.builder()
            .text("Test Message 1")
            .channelId(testChannel.getId())
            .authorId(testUser1.getId())
            .build());
        MessageDto.DetailResponse message2 = messageService.create(MessageDto.CreateRequest.builder()
            .text("Test Message 2")
            .channelId(testChannel.getId())
            .authorId(testUser1.getId())
            .additionalFiles(List.of(new MultipartFile() {
                // TODO 임시 binary 체크용 시간 남으면 정리
                @Override
                public String getName() {
                    return "testfile";
                }

                @Override
                public String getOriginalFilename() {
                    return "testfile.txt";
                }

                @Override
                public String getContentType() {
                    return "text/plain";
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public long getSize() {
                    return 0;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return "test file".getBytes();
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream("test file".getBytes());
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    try (FileOutputStream out = new FileOutputStream(dest)) {
                        out.write(getBytes());
                    }
                }
            }))
            .build());

        messageCreateTest(messageService, testChannel, testUser1);

        System.out.println("Message 목록 : " + messageService.findAllByChannelId(testChannel.getId()));
        System.out.println("Read Status 목록" + readStatusService.findAllByUserId(testUser1.getId()));
        MessageDto.DetailResponse target = messageService.findById(message1.getId());
        System.out.println("메세지 ID로 찾기 : " + target);
        if (target != null) {
            System.out.println("Test Target 메세지 변경 : " + messageService.update(
                MessageDto.UpdateRequest.builder()
                    .id(target.getId())
                    .text("Update Test Target Text~~")
                    .build()));
        }

        System.out.println("Message 목록 : " + messageService.findAllByChannelId(testChannel.getId()));

        System.out.println("Update Target Message 삭제");
        messageService.delete(target.getId());
        System.out.println("Message 목록 : " + messageService.findAllByChannelId(testChannel.getId()));

        messageService.deleteAll();
        System.out.println("============= 메세지 테스트 끝 =============");
    }
}
