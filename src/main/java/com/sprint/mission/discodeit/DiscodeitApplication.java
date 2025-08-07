package com.sprint.mission.discodeit;

import java.io.File;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.test.BasicMessageServiceTest;
import com.sprint.mission.discodeit.test.BinaryContentServiceTest;
import com.sprint.mission.discodeit.test.ReadStatusServiceTest;
import com.sprint.mission.discodeit.test.UserStatusServiceTest;

@SpringBootApplication
public class DiscodeitApplication {
	static {
		// GUI 환경 활성화
		System.setProperty("java.awt.headless", "false");
	}

	public static void main(String[] args) throws InterruptedException {
		cleanDirectory(new File("data/"));

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		Thread.sleep(1000);

		System.out.println("🚀 서비스 테스트 시작 🚀\n");

		// BinaryContentServiceTest.testBinaryContentService(context);
		// UserStatusServiceTest.testUserStatusService(context);
		// ReadStatusServiceTest.testReadStatusService(context);
		BasicMessageServiceTest.testBasicMessageService(context);

		System.out.println("✅ 모든 서비스 테스트 완료 ✅");

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
