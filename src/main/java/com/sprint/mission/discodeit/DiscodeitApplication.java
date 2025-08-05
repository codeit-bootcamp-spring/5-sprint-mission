package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.core.ChannelManageService;
import com.sprint.mission.discodeit.service.core.ChatService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {

	private final UserService userService;
	private final ChannelService channelService;
	private final MessageService messageService;
	private final ReadStatusService readStatusService;
	private final UserStatusService userStatusService;

	@Autowired
	public DiscodeitApplication(UserService userService, ChannelService channelService, MessageService messageService
		, UserStatusService userStatusService, ReadStatusService readStatusService) {

		this.userService = userService;
		this.channelService = channelService;
		this.messageService = messageService;
		this.userStatusService = userStatusService;
		this.readStatusService = readStatusService;
	}

	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, new String[0]);
	}

	@Override
	public void run(String... args) {
		testUserService();
		testChannelService();
		testMessageService();
		testCoreService();
	}


	static UserDto.DetailResponse setupUser(UserService userService) {
		UserDto.DetailResponse user = userService.create(
			UserDto.CreateRequest.builder()
				.name("test")
				.email("test@test.com")
				.password("test1234")
				.build()
		);
		return user;
	}

	static ChannelDto.DetailResponse setupChannel(ChannelService channelService, UserDto.DetailResponse user) {
		ChannelDto.DetailResponse channel = channelService.create(
			ChannelDto.CreateRequest.builder()
				.type(ChannelType.PUBLIC)
				.name("Test Channel")
				.description("Test Channel Description")
				.adminUserId(user.getId())
				.build());

		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
		System.out.println("메시지 생성: " + message.getId());
	}
//
//	public static void testJCFService() {
//
//		System.out.println("============= JCF Service 테스트 시작 =============");
//		JCFChannelRepository jcfChannelRepository = new JCFChannelRepository();
//		JCFUserRepository jcfUserRepository = new JCFUserRepository();
//		JCFMessageRepository jcfMessageRepository = new JCFMessageRepository();
//
//		JCFUserService userService = new JCFUserService(jcfUserRepository);
//		JCFChannelService channelService = new JCFChannelService(jcfChannelRepository);
//		JCFMessageService messageService = new JCFMessageService(jcfMessageRepository);
//
//		testUserService(userService);
//		testChannelService(channelService);
//		testMessageService(messageService);
//		testCoreService(messageService, userService, channelService);
//		System.out.println("============= JCF Service 테스트 끝 =============");
//	}
//
//	public static void testFileService() {
//		System.out.println("============= File Service 테스트 시작 =============");
//
//		FileChannelRepository fileChannelRepository = new FileChannelRepository();
//		FileUserRepository fileUserRepository = new FileUserRepository();
//		FileMessageRepository fileMessageRepository = new FileMessageRepository();
//
//		FileChannelService fileChannelService = new FileChannelService(fileChannelRepository);
//		FileUserService fileUserService = new FileUserService(fileUserRepository);
//		FileMessageService fileMessageService = new FileMessageService(fileMessageRepository);
//
//		testUserService(fileUserService);
//		testChannelService(fileChannelService);
//		testMessageService(fileMessageService);
//		testCoreService(fileMessageService, fileUserService, fileChannelService);
//		System.out.println("============= File Service 테스트 끝 =============");
//	}
//
//	public static void testBasicService() {
//
//		System.out.println("============= Basic JCF Service 테스트 시작 =============");
//
//		JCFChannelRepository jcfChannelRepository = new JCFChannelRepository();
//		JCFUserRepository jcfUserRepository = new JCFUserRepository();
//		JCFMessageRepository jcfMessageRepository = new JCFMessageRepository();
//
//		UserService jcfUserService = new BasicUserService(jcfUserRepository, null, null);
//		ChannelService jcfChannelService = new BasicChannelService(jcfChannelRepository);
//		MessageService jcfMessageService = new BasicMessageService(jcfMessageRepository);
//
//		testUserService(jcfUserService);
//		testChannelService(jcfChannelService);
//		testMessageService(jcfMessageService);
//		testCoreService(jcfMessageService, jcfUserService, jcfChannelService);
//
//		jcfMessageService.deleteAll();
//		jcfUserService.deleteAll();
//		jcfChannelService.deleteAll();
//
//		System.out.println("============= Basic JCF Service 테스트 시작 =============");
//
//		System.out.println("============= Basic File Service 테스트 시작 =============");
//
//		FileChannelRepository fileChannelRepository = new FileChannelRepository();
//		FileUserRepository fileUserRepository = new FileUserRepository();
//		FileMessageRepository fileMessageRepository = new FileMessageRepository();
//
//		UserService fileUserService = new BasicUserService(fileUserRepository, null, null);
//		ChannelService fileChannelService = new BasicChannelService(fileChannelRepository);
//		MessageService fileMessageService = new BasicMessageService(fileMessageRepository);
//
//		testUserService(fileUserService);
//		testChannelService(fileChannelService);
//		testMessageService(fileMessageService);
//		testCoreService(fileMessageService, fileUserService, fileChannelService);
//
//		fileMessageService.deleteAll();
//		fileUserService.deleteAll();
//		fileChannelService.deleteAll();
//
//		System.out.println("============= Basic File Service 테스트 끝 =============");
//	}

	public void testUserService() {

		System.out.println("============= 유저 테스트 시작 =============");

		UserDto.DetailResponse user = setupUser(userService);

		System.out.println("User 목록 : " + userService.findAll());

		UserDto.DetailResponse target = userService.findById(user.getId());
		System.out.println("유저 ID로 찾기 : " + target);
		if (target != null) {
			userService.update(target.getId(), "Update Target User", null);
			System.out.println("Test Target 유저 변경 : " + target);
		}

		System.out.println("User 목록 : " + userService.findAll());

		System.out.println("Update Target User 유저 삭제");
		userService.delete(target.getId());
		System.out.println("채널 목록 : " + userService.findAll());

		userService.deleteAll();

		System.out.println("============= 유저 테스트 끝 =============");

	}

	public void testChannelService() {

		System.out.println("============= 채널 테스트 시작 =============");

		UserDto.DetailResponse user = setupUser(userService);

		ChannelDto.DetailResponse testChannel = setupChannel(channelService, user);

		System.out.println("채널 목록 : " + channelService.findAll());

		ChannelDto.DetailResponse target = channelService.findById(testChannel.getId());
		System.out.println("채널 ID로 찾기 : " + target);
		if (target != null) {
			channelService.update(target.getId(), "Update Test Target", "Update Test Target");
			System.out.println("Test Target 채널 변경 : " + target);
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

		System.out.println("============= 메세지 테스트 시작 =============");

		User testUser1 = new User("Test1", "test1@test.com", "tester1", null);
		User testUser2 = new User("Test2", "test2@test.com", "tester2", null);

		Channel testChannel = new Channel(ChannelType.PUBLIC, "Test Channel", "Test Channel"
			, testUser1.getId(), List.of(testUser1.getId(), testUser2.getId()), null);

		Message message1 = new Message("Test Target Message From User1", testChannel.getId(), testUser1.getId());
		Message message2 = new Message("Test Message From User2", testChannel.getId(), testUser2.getId());

		testChannel.addMessage(message1.getId());
		testChannel.addMessage(message2.getId());

		messageService.create(message1);
		messageService.create(message2);

		messageCreateTest(messageService, testChannel, testUser1);

		System.out.println("Message 목록 : " + messageService.getAll());

		Message target = messageService.get(message1.getId());
		System.out.println("메세지 ID로 찾기 : " + target);
		if (target != null) {
			messageService.update(target.getId(), "Update Target Text~~");
			System.out.println("Test Target 메세지 변경 : " + target);
		}

		System.out.println("Message 목록 : " + messageService.getAll());

		System.out.println("Update Target Message 삭제");
		messageService.delete(target.getId());
		System.out.println("Message 목록 : " + messageService.getAll());

		messageService.deleteAll();
		System.out.println("============= 메세지 테스트 끝 =============");

	}

	public void testCoreService() {

		System.out.println("============= 채팅, 채널관리 테스트 시작 =============");

		ChannelManageService channelManageService = new ChannelManageService(channelService, userService);
		ChatService chatService = new ChatService(messageService, channelService);

		User adminUser = new User("admin", "admin@test.com", "admin", null);
		User testUser = new User("tester", "test@test.com", "test", null);
		userService.create(adminUser);
		userService.create(testUser);

		Channel channel = new Channel(ChannelType.PUBLIC, "general", "메인 채팅방", adminUser.getId());
		channelService.create(channel);

		channelManageService.addUserToChannel(channel.getId(), testUser.getId());

		List<User> usersInChannel = channelManageService.listUsersInChannel(channel.getId());
		System.out.println("채널 유저 목록:");
		usersInChannel.forEach(u -> System.out.println("- " + u.getName()));

		chatService.sendMessage(channel.getId(), adminUser.getId(), "Hi, I'm adminUser");
		chatService.sendMessage(channel.getId(), testUser.getId(), "Hi, I'm testUser");

		System.out.println("\n채널 메시지 목록:");
		List<Message> messages = chatService.getMessagesInChannel(channel.getId());
		for (Message m : messages) {
			String userName = userService.get(m.getAuthorId()).getName();
			System.out.printf("[%d] %s: %s\n", m.getCreatedAt(), userName, m.getText());
		}

		channelManageService.removeUserFromChannel(channel.getId(), testUser.getId());
		System.out.println("\ntestUser 퇴장 후 채널 유저 목록:");
		channelManageService.listUsersInChannel(channel.getId())
			.forEach(u -> System.out.println("- " + u.getName()));

		userService.deleteAll();
		channelService.deleteAll();
		System.out.println("============= 채팅, 채널관리 테스트 끝 =============");
	}
}
