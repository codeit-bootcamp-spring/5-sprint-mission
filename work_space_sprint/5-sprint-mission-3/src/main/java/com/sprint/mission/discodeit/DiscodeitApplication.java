package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService =  context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		legacyTest(userService, channelService, messageService);

		System.out.println("http://localhost:8080/");
	}

	private static void legacyTest(UserService userService, ChannelService channelService, MessageService messageService) {
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		messageCreateTest(messageService, channel, user);
	}

	static User setupUser(UserService userService) {
		User user = userService.create("ㅔ홍길동", "hong1234@gmail.com", "hong1234");
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create(ChannelType.PUBLIC, "알림", "공지 채널입니다.");
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create("안녕하십니까?", channel.getId(), author.getId());
		System.out.println("메시지 ID : " + message.getId() + "\n" + message.getContent() + " - " + author.getUsername());
	}
}
