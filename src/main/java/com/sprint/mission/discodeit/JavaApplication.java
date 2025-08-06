package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.util.List;
import java.util.stream.Collectors;

public class JavaApplication {

    public static void main(String[] args) {
        // ==================================================
        // 1. File Repository 기반 테스트
        // ==================================================
        System.out.println("\n=========== FileRepository Test ===========\n");

        // 1-1. 의존성 설정 (File-based Dependency Configuration)
        UserRepository fileUserRepository = new FileUserRepository();
        ChannelRepository fileChannelRepository = new FileChannelRepository();
        MessageRepository fileMessageRepository = new FileMessageRepository();

        // 1-2. 서비스 초기화 (Service Initialization)
        UserService fileUserService = new BasicUserService(fileUserRepository);
        ChannelService fileChannelService = new BasicChannelService(fileChannelRepository);
        MessageService fileMessageService = new BasicMessageService(fileMessageRepository, fileUserRepository, fileChannelRepository);

        // 1-3. 시나리오별 테스트 실행 (Execute Scenarios)
        testUserScenarios(fileUserService);
        testChannelScenarios(fileChannelService, fileUserService);
        testMessageScenarios(fileMessageService, fileChannelService, fileUserService);


        // ==================================================
        // 2. JCF Repository 기반 테스트
        // ==================================================
        System.out.println("\n=========== JCFRepository Test ===========\n");

        // 2-1. 의존성 설정 (JCF-based Dependency Configuration)
        UserRepository jcfUserRepository = new JCFUserRepository();
        ChannelRepository jcfChannelRepository = new JCFChannelRepository();
        MessageRepository jcfMessageRepository = new JCFMessageRepository();

        // 2-2. 서비스 초기화 (Service Initialization)
        UserService jcfUserService = new BasicUserService(jcfUserRepository);
        ChannelService jcfChannelService = new BasicChannelService(jcfChannelRepository);
        MessageService jcfMessageService = new BasicMessageService(jcfMessageRepository, jcfUserRepository, jcfChannelRepository);

        // 2-3. 시나리오별 테스트 실행 (Execute Scenarios)
        testUserScenarios(jcfUserService);
        testChannelScenarios(jcfChannelService, jcfUserService);
        testMessageScenarios(jcfMessageService, jcfChannelService, jcfUserService);
    }

    /**
     * 사용자(User) 관련 전체 시나리오를 테스트합니다.
     */
    private static void testUserScenarios(UserService userService) {
        System.out.println("--- Testing User Scenarios ---");
        userService.clear();

        // 테스트 데이터 생성
        User user1 = userService.create("user1", "pass1");
        System.out.println("사용자 생성: " + user1);
        System.out.println("사용자 단건 조회: " + userService.find(user1.getId()));

        User user2 = userService.create("user2", "pass2");
        System.out.println("추가 사용자 생성: " + user2);
        System.out.println("사용자 전체 조회: " + formatList(userService.findAll()));

        User updatedUser2 = userService.update(user2.getId(), "updatedUser2", "newPass");
        System.out.println("사용자 수정: " + updatedUser2);

        userService.delete(user2.getId());
        System.out.println("사용자 삭제 후 전체 조회: " + formatList(userService.findAll()));
    }

    /**
     * 채널(Channel) 관련 전체 시나리오를 테스트합니다.
     */
    private static void testChannelScenarios(ChannelService channelService, UserService userService) {
        System.out.println("\n--- Testing Channel Scenarios ---");
        channelService.clear();
        userService.clear();

        // 테스트 데이터 생성
        User owner = userService.create("channelOwner", "ownerPass");
        Channel channel1 = channelService.create("channel1", "desc1");
        System.out.println("채널 생성: " + channel1);
        System.out.println("채널 단건 조회: " + channelService.find(channel1.getId()));

        Channel channel2 = channelService.create("channel2", "desc2");
        System.out.println("추가 채널 생성: " + channel2);
        System.out.println("채널 전체 조회: " + formatList(channelService.findAll()));

        Channel updatedChannel2 = channelService.update(channel2.getId(), "updatedChannel2", "newDesc");
        System.out.println("채널 수정: " + updatedChannel2);

        channelService.delete(channel2.getId());
        System.out.println("채널 삭제 후 전체 조회: " + formatList(channelService.findAll()));
    }

    /**
     * 메시지(Message) 관련 전체 시나리오를 테스트합니다.
     */
    private static void testMessageScenarios(MessageService messageService, ChannelService channelService, UserService userService) {
        System.out.println("\n--- Testing Message Scenarios ---");
        messageService.clear();
        channelService.clear();
        userService.clear();

        // 테스트 데이터 생성
        User author = userService.create("author", "authorPass");
        Channel channel = channelService.create("message-channel", "msg-desc");

        Message message1 = messageService.create("Hello World!", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message1);
        System.out.println("메시지 단건 조회: " + messageService.find(message1.getId()));

        Message message2 = messageService.create("Second message.", channel.getId(), author.getId());
        System.out.println("추가 메시지 생성: " + message2);
        // JCF/File MessageRepository 에는 findByChannelId 가 없으므로 findAll 로 대체합니다.
        System.out.println("메시지 전체 조회: " + formatList(messageService.findAll()));

        Message updatedMessage2 = messageService.update(message2.getId(), "Updated second message.");
        System.out.println("메시지 수정: " + updatedMessage2);

        messageService.delete(message2.getId());
        System.out.println("메시지 삭제 후 전체 조회: " + formatList(messageService.findAll()));
    }

    private static <T> String formatList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return list.stream()
                .map(T::toString)
                .collect(Collectors.joining("\n"));
    }
}