package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// 서비스 초기화
		UserService userService = context.getBean("basicUserService", UserService.class);
		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);

		// 시나리오별 테스트 실행 (Execute Scenarios)
		testUserScenarios(userService);
		testChannelScenarios(channelService, userService);
		testMessageScenarios(messageService, channelService, userService);
	}

	/**
	 * 사용자(User) 관련 전체 시나리오를 테스트합니다.
	 */
	private static void testUserScenarios(UserService userService) {
		System.out.println("--- Testing User Scenarios ---");
		userService.clear();

		// 테스트 데이터 생성
		UserResponse user1 = userService.create(new UserCreateRequest("user1", "pass1", null));
		System.out.println("사용자 생성: " + user1);
		System.out.println("사용자 단건 조회: " + userService.find(user1.getId()));

		UserResponse user2 = userService.create(new UserCreateRequest("user2", "pass2", null));
		System.out.println("추가 사용자 생성: " + user2);
		System.out.println("사용자 전체 조회: " + formatList(userService.findAll()));

		UserResponse updatedUser2 = userService.update(new UserUpdateRequest(user2.getId(), "updatedUser2", "newPass", null));
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
		UserResponse owner = userService.create(new UserCreateRequest("channelOwner", "ownerPass", null));
		ChannelResponse channel1 = channelService.createPublicChannel(new ChannelPublicCreateRequest("channel1", "desc1"));
		System.out.println("채널 생성: " + channel1);
		System.out.println("채널 단건 조회: " + channelService.find(channel1.getId()));

		ChannelResponse channel2 = channelService.createPublicChannel(new ChannelPublicCreateRequest("channel2", "desc2"));
		System.out.println("추가 채널 생성: " + channel2);
		System.out.println("채널 전체 조회: " + formatList(channelService.findAll()));

		ChannelResponse updatedChannel2 = channelService.update(new ChannelUpdateRequest(channel2.getId(), "updatedChannel2", "newDesc"));
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
		UserResponse author = userService.create(new UserCreateRequest("author", "authorPass", null));
		ChannelResponse channel = channelService.createPublicChannel(new ChannelPublicCreateRequest("message-channel", "msg-desc"));

		MessageResponse message1 = messageService.create(new MessageCreateRequest("Hello World!", channel.getId(), author.getId(), java.util.Collections.emptyList()));
		System.out.println("메시지 생성: " + message1);
		System.out.println("메시지 단건 조회: " + messageService.find(message1.getId()));

		MessageResponse message2 = messageService.create(new MessageCreateRequest("Second message.", channel.getId(), author.getId(), null));
		System.out.println("추가 메시지 생성: " + message2);
		// JCF/File MessageRepository 에는 findByChannelId 가 없으므로 findAll 로 대체합니다.
		System.out.println("메시지 전체 조회: " + formatList(messageService.findAll()));

		MessageResponse updatedMessage2 = messageService.update(new MessageUpdateRequest(message2.getId(), "Updated second message."));
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
