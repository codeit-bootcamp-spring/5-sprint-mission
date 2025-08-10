package com.sprint.mission.discodeit.test;

import java.io.File;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.request.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.JoinChannelRequest;
import com.sprint.mission.discodeit.dto.request.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessagesByChannelIdRequest;
import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.JoinChannelResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.dto.response.user.CreateUserResponse;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class BasicChatTest {

	public static void BasicChat(ConfigurableApplicationContext context) {
		System.out.println("기본 채팅 테스트");
		System.out.println();

		// 서비스 빈들 가져오기
		UserService userService = context.getBean(UserService.class);
		AuthService authService = context.getBean(AuthService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		try {
			System.out.println("1. 회원가입 - 홍길동");

			CreateUserRequest hongRequest = CreateUserRequest.builder()
				.loginId("hong")
				.password("1234")
				.defaultNickname("홍길동")
				.email("hong@hh.com")
				.build();

			CreateUserResponse hongResponse = userService.createUser(hongRequest);

			System.out.println("홍길동 회원가입: " + (hongResponse != null ? "성공" : "실패"));
			System.out.println("users.ser 파일: " + (new File("data/users.ser").exists() ? "생성됨" : "없음"));
			System.out.println("---------------------------------");

			System.out.println("2. 회원가입 - 정영진");

			CreateUserRequest jeongRequest = CreateUserRequest.builder()
				.loginId("jeong")
				.password("1234")
				.defaultNickname("정영진")
				.email("jeong@jj.com")
				.build();

			CreateUserResponse jeongResponse = userService.createUser(jeongRequest);

			System.out.println("정영진 회원가입: " + (jeongResponse != null ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("3. 로그인");
			LoginRequest loginRequest = LoginRequest.builder()
				.loginId("hong")
				.password("1234")
				.build();

			LoginResponse loginResponse = authService.login(loginRequest);
			System.out.println("홍길동 로그인: " + (loginResponse != null && loginResponse.isSuccess() ? "성공" : "실패"));
			if (loginResponse != null) {
				System.out.println("로그인 사용자: " + loginResponse.getDefaultNickname());
			}
			System.out.println("---------------------------------");

			System.out.println("4. 채널 생성");

			CreatePublicChannelRequest channelRequest = CreatePublicChannelRequest.builder()
				.channelName("일반")
				.build();

			CreateChannelResponse channelResponse = channelService.createPublicChannel(channelRequest);

			System.out.println("일반 채널 생성: " + (channelResponse != null ? "성공" : "실패"));
			System.out.println("channels.ser 파일: " + (new File("data/channels.ser").exists() ? "생성됨" : "없음"));
			System.out.println("---------------------------------");

			System.out.println("5. 홍길동 채널 참여");
			JoinChannelRequest hongJoinRequest = JoinChannelRequest.builder()
				.userId(hongResponse.getId())
				.userDefaultNickname("홍길동")
				.channelName("일반")
				.build();

			JoinChannelResponse hongJoinResponse = channelService.joinChannel(hongJoinRequest);
			System.out.println("홍길동 채널 참여: " + (hongJoinResponse != null && hongJoinResponse.isSuccess() ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("6. 정영진 채널 참여");
			JoinChannelRequest jeongJoinRequest = JoinChannelRequest.builder()
				.userId(jeongResponse.getId())
				.userDefaultNickname("정영진")
				.channelName("일반")
				.build();

			JoinChannelResponse jeongJoinResponse = channelService.joinChannel(jeongJoinRequest);
			System.out.println("정영진 채널 참여: " + (jeongJoinResponse != null && jeongJoinResponse.isSuccess() ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("7. 메시지 작성 - 홍길동");

			CreateMessageRequest hongMessageRequest = CreateMessageRequest.builder()
				.authorId(hongResponse.getId())
				.channelId(channelResponse.getId())
				.text("안녕하세요!")
				.build();

			MessageResponse hongMessageResponse = messageService.createMessage(hongMessageRequest);

			System.out.println("홍길동 메시지: " + (hongMessageResponse != null ? "성공" : "실패"));
			System.out.println("messages.ser 파일: " + (new File("data/messages.ser").exists() ? "생성됨" : "없음"));
			System.out.println("---------------------------------");

			System.out.println("8. 메시지 작성 - 정영진");

			CreateMessageRequest jeongMessageRequest = CreateMessageRequest.builder()
				.authorId(jeongResponse.getId())
				.channelId(channelResponse.getId())
				.text("안녕하세요, 홍길동님!")
				.build();

			MessageResponse jeongMessageResponse = messageService.createMessage(jeongMessageRequest);

			System.out.println("정영진 답장: " + (jeongMessageResponse != null ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("9. 메시지 조회 및 최종 상태");
			GetMessagesByChannelIdRequest getMessagesRequest = GetMessagesByChannelIdRequest.builder()
				.channelId(channelResponse.getId())
				.build();

			List<MessageResponse> messages = messageService.getAllByChannelId(getMessagesRequest);

			System.out.println("채널 '일반'의 메시지 목록:");
			for (int i = 0; i < messages.size(); i++) {
				MessageResponse msg = messages.get(i);
				String authorName = msg.getAuthorId().equals(hongResponse.getId()) ? "홍길동" : "정영진";
				System.out.println("[" + (i+1) + "] " + authorName + ": " + msg.getText());
			}
			System.out.println("메시지 조회: " + (messages.size() == 2 ? "성공" : "실패"));

			System.out.println("BasicCaht 완료");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
