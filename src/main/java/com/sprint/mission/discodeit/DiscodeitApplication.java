package com.sprint.mission.discodeit;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.test.MessageTest;
import com.sprint.mission.discodeit.test.PrivateChannelTest;

@SpringBootApplication
public class DiscodeitApplication {
	static {
		// GUI 환경 활성화
		System.setProperty("java.awt.headless", "false");
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		cleanDirectory(new File("data/"));

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		Thread.sleep(1000);

		System.out.println("서비스 테스트 시작\n");

		// BasicChatScenarioTest.BasicChat(context);
		// PrivateChannelTest.privateChannel(context);
		MessageTest.message(context);

		System.out.println("모든 서비스 테스트 완료");

		cleanDirectory(new File("data/"));
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
