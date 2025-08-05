package com.sprint.mission.discodeit;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.request.user.*;
import com.sprint.mission.discodeit.dto.response.user.*;
import com.sprint.mission.discodeit.service.*;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		cleanDirectory(new File("data/"));

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		AuthService authService = context.getBean(AuthService.class);


		// 테스트용 유저 생성 요청
		CreateUserRequest request = new CreateUserRequest(
			"testLoginId",
			"testPassword",
			"testNickname",
			"testEmail@example.com",
			null
		);

		CreateUserResponse response = userService.createUser(request);
		System.out.println("생성된 유저: " + response);

		System.out.println("---------------------------------");

		CreateUserRequest createRequest = new CreateUserRequest(
			"testLoginId2",
			"testPassword2",
			"testNickname2",
			"testEmail2@example.com",
			null
		);
		CreateUserResponse createResponse = userService.createUser(createRequest);



		// 생성된 유저의 ID로 조회
		GetUserByIdRequest getRequest = new GetUserByIdRequest(createResponse.getId());

		GetUserResponse getUserResponse = userService.getUserById(getRequest);

		System.out.println("조회된 유저: " + getUserResponse);
		System.out.println(getRequest.getId().equals(getUserResponse.getId()));

		System.out.println("---------------------------------");
		GetUserByLoginIdRequest getRequest2 = new GetUserByLoginIdRequest(createRequest.getLoginId());

		GetUserResponse response2= userService.getUserByLoginId(getRequest2);

		System.out.println("조회된 유저: " + response2);
		System.out.println(createResponse.getId().equals(response2.getId()));

		System.out.println("---------------------------------");
		List<GetUserResponse> allUsers = userService.getAllUsers();
		System.out.println("전체 유저 목록:");
		for (GetUserResponse user : allUsers) {
			System.out.println(user);
		}

		System.out.println("---------------------------------");
		
		// 기존 유저 정보
		String loginId = "testLoginId2";
		String oldPassword = "testPassword2";
		String newPassword = "newTestPassword2";

		// 1. 기존 비밀번호로 로그인
		LoginRequest loginRequest = new LoginRequest(loginId, oldPassword);
		LoginResponse loginResponse = authService.login(loginRequest);
		System.out.println("기존 비밀번호로 로그인 성공: " + loginResponse);

		// 2. 비밀번호 변경
		UpdateUserPasswordRequest updateRequest = new UpdateUserPasswordRequest(
			loginResponse.getId(), // 로그인 응답에서 유저 ID 사용
			oldPassword,
			newPassword
		);
		UpdateUserPasswordResponse updateResponse = userService.updateUserPassword(updateRequest);
		System.out.println("비밀번호 변경 성공: " + updateResponse.isSuccess());

		// 3. 변경된 비밀번호로 로그인
		LoginRequest loginRequestNew = new LoginRequest(loginId, newPassword);
		LoginResponse loginResponseNew = authService.login(loginRequestNew);
		System.out.println("변경된 비밀번호로 로그인 성공: " + loginResponseNew);
		
		System.out.println("---------------------------------");

		getUserResponse = userService.getUserById(getRequest);
		
		// 삭제할 유저의 ID
		UUID userId = getUserResponse.getId();


		// 삭제 요청 생성
		DeleteUserByIdRequest deleteRequest = new DeleteUserByIdRequest(userId);

		// 삭제 실행
		DeleteUserResponse deleteResponse = userService.deleteUser(deleteRequest);

		// 결과 출력
		System.out.println("삭제 성공 여부: " + deleteResponse.isSuccess());
		try {
			getUserResponse = userService.getUserById(getRequest);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		System.out.println("---------------------------------");
		allUsers = userService.getAllUsers();
		System.out.println("전체 유저 목록:");
		for (GetUserResponse user : allUsers) {
			System.out.println(user);
		}

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
