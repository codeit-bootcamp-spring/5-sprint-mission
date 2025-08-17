package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {

	static User setupUser(UserService userService) {
		var req = new UserCreateRequest("woody", "woody@codeit.com", "woody1234");
		return userService.create(req, Optional.empty()); // 프로필 바이너리 없음
	}

	static Channel setupChannel(ChannelService channelService) {
		var req = new PublicChannelCreateRequest("공지", "공지 채널입니다.");
		return channelService.create(req);
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		var req = new MessageCreateRequest("안녕하세요.", channel.getId(), author.getId());
		Message message = messageService.create(req, Collections.emptyList()); // 첨부 없음
		System.out.println("메시지 생성: " + message.getId());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// 샘플 데이터 시드
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		messageCreateTest(messageService, channel, user);
	}
}
