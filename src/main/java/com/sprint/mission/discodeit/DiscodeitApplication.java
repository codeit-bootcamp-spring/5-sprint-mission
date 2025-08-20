package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    static List<UserResponseDto> setupUser(UserService userService,
                                           BinaryContentRepository binaryContentRepository,
                                           UserStatusRepository userStatusRepository) {
        List<UserResponseDto> users = new ArrayList<>();

        // 각 사용자별 프로필 이미지 파일명과 MIME 타입
        String[][] userProfiles = {
                {"woody.jpg", "image/jpeg"},
                {"buzz.jpeg", "image/jpeg"},
                {"jessie.jpg", "image/jpeg"},
                {"bullseye.jpg", "image/jpeg"},
                {"slinky.webp", "image/webp"}
        };

        String[] usernames = {"woody", "buzz", "jessie", "bullseye", "slinky"};
        String[] emails = {"woody@codeit.com", "buzz@codeit.com", "jessie@codeit.com", "bullseye@codeit.com", "slinky@codeit.com"};
        String defaultPassword = "1234";

        for (int i = 0; i < usernames.length; i++) {
            byte[] profileBytes = loadResourceBytes(userProfiles[i][0]);
            BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
                    userProfiles[i][0], userProfiles[i][1], profileBytes
            );

            UserCreateRequest userRequest = new UserCreateRequest(usernames[i], emails[i], defaultPassword);
            UserResponseDto user = userService.create(userRequest, Optional.of(profileRequest));

            users.add(user);

            // 온라인 상태 초기화
            UserStatus status = new UserStatus(user.id(), Instant.now());
            userStatusRepository.save(status);
        }

        return users;
    }

    static List<ChannelResponseDto> setupChannel(ChannelService channelService) {
        List<ChannelResponseDto> channels = new ArrayList<>();
        channels.add(channelService.create(new ChannelCreateRequest("공지", "공지 채널입니다.")));
        channels.add(channelService.create(new ChannelCreateRequest("자유", "자유롭게 이야기하는 채널입니다.")));
        channels.add(channelService.create(new ChannelCreateRequest("질문", "질문과 답변을 위한 채널입니다.")));
        channels.add(channelService.create(new ChannelCreateRequest("팀채널", "팀 내부용 채널입니다.")));
        channels.add(channelService.create(new ChannelCreateRequest("이벤트", "이벤트 관련 채널입니다.")));
        return channels;
    }

    private static byte[] loadResourceBytes(String filename) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String path = "static/" + filename; // static 폴더 안
        try (InputStream in = cl.getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException(filename + "를 찾을 수 없습니다. 경로: " + path);
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 서비스 초기화
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        BinaryContentRepository binaryContentRepository = context.getBean(BinaryContentRepository.class);
        UserStatusRepository userStatusRepository = context.getBean(UserStatusRepository.class);

        // 셋업
        List<UserResponseDto> users = setupUser(userService, binaryContentRepository, userStatusRepository);
        List<ChannelResponseDto> channels = setupChannel(channelService);

        String port = context.getEnvironment().getProperty("server.port", "8080");
        System.out.println("서버 실행 중 => http://localhost:" + port);
    }
}

