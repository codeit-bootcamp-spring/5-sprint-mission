package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.test.ChannelServiceTest;
import com.sprint.mission.discodeit.test.MessageServiceTest;
import com.sprint.mission.discodeit.test.UserServiceTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		UserServiceTest userServiceTest = new UserServiceTest(userService);
		userServiceTest.runAllTest();
		System.out.println("-----------------------------------------\n");

		ChannelServiceTest channelServiceTest = new ChannelServiceTest(channelService);
		channelServiceTest.runAllTest();
		System.out.println("-----------------------------------------\n");

		MessageServiceTest messageServiceTest = new MessageServiceTest(messageService, userService, channelService);
		messageServiceTest.runAllTest();
	}

}
