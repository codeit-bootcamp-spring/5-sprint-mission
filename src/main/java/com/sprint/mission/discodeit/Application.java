package com.sprint.mission.discodeit;

import java.io.File;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.*;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// SpringApplication.run(Application.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);


		UserService userService = context.getBean("basicUserService",UserService.class);


		// 1. 정상 회원가입 테스트
		try {
			UserCreateRequest req = UserCreateRequest.builder()
				.loginId("user1")
				.password("pw1")
				.nickname("닉네임1")
				.email("user1@email.com")
				.build();
			userService.createUser(req);
			System.out.println("정상 회원가입 성공");
		} catch (Exception e) {
			System.out.println("정상 회원가입 실패: " + e.getMessage());
		}

		// 2. 아이디 중복 테스트
		try {
			UserCreateRequest req1 = UserCreateRequest.builder()
				.loginId("dupId")
				.password("pw1")
				.nickname("닉네임1")
				.email("dup1@email.com")
				.build();
			UserCreateRequest req2 = UserCreateRequest.builder()
				.loginId("dupId")
				.password("pw2")
				.nickname("닉네임2")
				.email("dup2@email.com")
				.build();
			userService.createUser(req1);
			userService.createUser(req2);
			System.out.println("아이디 중복 예외가 발생하지 않았습니다.");
		} catch (Exception e) {
			System.out.println("아이디 중복 예외 발생: " + e.getMessage());
		}

		// 3. 이메일 중복 테스트
		try {
			UserCreateRequest req1 = UserCreateRequest.builder()
				.loginId("emailId1")
				.password("pw1")
				.nickname("닉네임1")
				.email("dup@email.com")
				.build();
			UserCreateRequest req2 = UserCreateRequest.builder()
				.loginId("emailId2")
				.password("pw2")
				.nickname("닉네임2")
				.email("dup@email.com")
				.build();
			userService.createUser(req1);
			userService.createUser(req2);
			System.out.println("이메일 중복 예외가 발생하지 않았습니다.");
		} catch (Exception e) {
			System.out.println("이메일 중복 예외 발생: " + e.getMessage());
		}

		// 4. profileImage 없는 경우 테스트
		try {
			UserCreateRequest req = UserCreateRequest.builder()
				.loginId("noimg")
				.password("pw")
				.nickname("이미지없음")
				.email("noimg@email.com")
				// .profileImage(null) // 명시적으로 안 넣음
				.build();
			userService.createUser(req);
			System.out.println("profileImage 없이 회원가입 성공");
		} catch (Exception e) {
			System.out.println("profileImage 없이 회원가입 실패: " + e.getMessage());
		}





		cleanDirectory(new File("data/"));

	}

	public static void cleanDirectory(File directory) {
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
