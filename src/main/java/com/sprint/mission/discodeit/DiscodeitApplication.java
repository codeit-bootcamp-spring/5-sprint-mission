package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ReadStatusService readStatusService;
    private final BinaryContentService binaryContentService;


    public DiscodeitApplication(
            UserService userService,
            UserStatusService userStatusService,
            ChannelService channelService,
            MessageService messageService,
            ReadStatusService readStatusService,
            BinaryContentService binaryContentService
    ) {
        this.userService = userService;
        this.userStatusService = userStatusService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.readStatusService = readStatusService;
        this.binaryContentService = binaryContentService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Override
    public void run(String... args) {

        testUser();
//        testChannel();

    }

    private void testUser() {
        System.out.println("🚀 유저 테스트 🚀 \n");

        // 전체 유저 조회
        List<UserDto.View> userList = userService.findAll();
        System.out.println(userList);

        boolean isOnline = userStatusService.isOnline(userList.get(0).id());
        System.out.println(isOnline);

        // 1. Create DTO 생성
        var dto = new UserDto.Create("소연3", "soyeon3@gmail.com", "1234", null);

        // 2. 사용자 생성
        User user = userService.create(dto);
        UUID id = user.getId();
        String email = user.getEmail();

        // 3. 이메일로 조회
        Optional<UserDto.View> byEmail = userService.findByEmail(email);
        if (byEmail.isEmpty()) {
            throw new RuntimeException("존재하지 않는 사용자 입니다.");
        }
        System.out.println("--- 이메일 조회 결과 ---");
        System.out.println(byEmail.get());

        // 4. ID로 조회
        Optional<UserDto.View> byId = userService.findById(id);
        if (byId.isEmpty()) {
            throw new RuntimeException("존재하지 않는 사용자 입니다.");
        }
        System.out.println("--- ID 조회 결과 ---");
        System.out.println(byId.get());

        System.out.println("🎉 테스트 완료");
    }

    private void testChannel() {
        System.out.println("🚀 채널 테스트 🚀 \n");

        // 1. 모든 채널 조회
        System.out.println("----- 채널 목록 ----");
        List<Channel> channelList = channelService.findAll();
        System.out.println(channelList);

        // 1. Dto 생성
        var dto = new ChannelDto.Create("공부채널", ChannelType.TEXT, null, null);

        // 2. 채널 생성
        Channel created = channelService.create(dto);
        UUID id = created.getId(); // 채널 아이디
        System.out.println("---생성된 채널---");
        System.out.println(created);

        // 3. 채널 토픽 업데이트
        channelService.updateTopic(id, "공부채널");

        // 4. 채널 설명 업데이트
        channelService.updateDescription(id, "공부채널 입니다.");

        // 아이디로 채널 조회
        Channel foundById = channelService.findById(id)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));
        System.out.println("--- ID로 조회한 채널 ---");
        System.out.println(foundById);

        // 이름으로 채널 조회
        List<Channel> foundByName = channelService.findByName("공부채널");
        System.out.println("--- 이름으로 조회한 채널 ---");
        System.out.println(foundByName);

        // 8. 채널 삭제
        boolean result = channelService.delete(id);
        if (result) {
            System.out.println("삭제완료");
        }
    }
}
