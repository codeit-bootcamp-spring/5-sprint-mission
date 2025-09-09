package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) throws InterruptedException, IOException {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

//		testing(context);

	}

	private static void testing(ConfigurableApplicationContext context) throws InterruptedException {
		// cleanDirectory(new File("data/"));
		Thread.sleep(10000);

		System.out.println("서비스 테스트 시작\n");

		UserStatusService userStatusService = context.getBean(UserStatusService.class);
		UserRepository userRepository = context.getBean(UserRepository.class);
		System.out.println();
		System.out.println("유저 상태 정보 : " + userStatusService.getAll());
		System.out.println("유저 정보 : " + userRepository.findAll());
		System.out.println();

		System.out.println("모든 서비스 테스트 완료");

		// cleanDirectory(new File("data/"));
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
