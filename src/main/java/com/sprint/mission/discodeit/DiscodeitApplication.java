package com.sprint.mission.discodeit;

import java.io.File;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		cleanDirectory(new File("data/"));

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		AuthService authService = context.getBean(AuthService.class);

		cleanDirectory(new File("data/"));
	}

	// 채널 목록을 출력하는 메서드
	private static void printUserChannels(String title, List<ChannelResponse> channels) {
		System.out.println(title);
		for (ChannelResponse channel : channels) {
			System.out.println(channel);
		}
	}

	private static void cleanDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						cleanDirectory(file);
					} else {
						file.delete();
					}
				}
			}
			directory.delete();
		}
	}
}
