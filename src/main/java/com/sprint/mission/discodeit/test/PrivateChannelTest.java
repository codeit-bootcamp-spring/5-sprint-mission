package com.sprint.mission.discodeit.test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.request.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.GetChannelsByUserRequest;
import com.sprint.mission.discodeit.dto.request.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessagesByChannelIdRequest;
import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.dto.response.user.CreateUserResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class PrivateChannelTest {

	public static void privateChannel(ConfigurableApplicationContext context) {
		System.out.println("프라이빗 채널");
		System.out.println();

		// 서비스 빈들 가져오기
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		try {
			System.out.println("1. 회원가입 - 김영태");
			CreateUserRequest kimytRequest = CreateUserRequest.builder()
				.loginId("kimyt")
				.password("1234")
				.defaultNickname("김영태")
				.email("kimyt@kim.com")
				.build();

			CreateUserResponse kimytResponse = userService.createUser(kimytRequest);
			System.out.println("김영태 회원가입: " + (kimytResponse != null ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("2. 회원가입 - 김민철");
			CreateUserRequest kimmcRequest = CreateUserRequest.builder()
				.loginId("kimmc")
				.password("1234")
				.defaultNickname("김민철")
				.email("kimmc@kim.com")
				.build();

			CreateUserResponse kimmcResponse = userService.createUser(kimmcRequest);
			System.out.println("김민철 회원가입: " + (kimmcResponse != null ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("3. 회원가입 - 이동재");
			CreateUserRequest leedjRequest = CreateUserRequest.builder()
				.loginId("leedj")
				.password("1234")
				.defaultNickname("이동재")
				.email("leedj@lee.com")
				.build();

			CreateUserResponse leedjResponse = userService.createUser(leedjRequest);
			System.out.println("이동재 회원가입: " + (leedjResponse != null ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("4. 프라이빗 채널 생성");
			CreatePrivateChannelRequest privateChannelRequest = CreatePrivateChannelRequest.builder()
				.memberIds(Arrays.asList(kimytResponse.getId(), kimmcResponse.getId()))
				.build();

			CreateChannelResponse channelResponse = channelService.createPrivateChannel(privateChannelRequest);
			System.out.println("프라이빗 채널 생성: " + (channelResponse != null ? "성공" : "실패"));
			System.out.println("channels.ser 파일: " + (new File("data/channels.ser").exists() ? "생성됨" : "없음"));
			System.out.println("readStatus.ser 파일: " + (new File("data/readStatus.ser").exists() ? "생성됨" : "없음"));
			if (channelResponse != null) {
				System.out.println("채널 ID: " + channelResponse.getId());
				System.out.println("채널 타입: " + channelResponse.getType());
			}
			System.out.println("---------------------------------");

			System.out.println("5. 메시지 작성 - 김영태");
			CreateMessageRequest kimytMessageRequest = CreateMessageRequest.builder()
				.authorId(kimytResponse.getId())
				.channelId(channelResponse.getId())
				.text("private 채팅임")
				.build();

			MessageResponse kimytMessageResponse = messageService.createMessage(kimytMessageRequest);
			System.out.println("김영태 메시지 작성: " + (kimytMessageResponse != null ? "성공" : "실패"));
			if (kimytMessageResponse != null) {
				System.out.println("저장된 메시지 내용: \"" + kimytMessageResponse.getText() + "\" - (성공)");
			}
			System.out.println("messages.ser 파일: " + (new File("data/messages.ser").exists() ? "생성됨" : "없음"));
			System.out.println("---------------------------------");

			System.out.println("6. 메시지 작성 - 김민철");
			CreateMessageRequest kimmcMessageRequest = CreateMessageRequest.builder()
				.authorId(kimmcResponse.getId())
				.channelId(channelResponse.getId())
				.text("ㅇㅇ private하네")
				.build();

			MessageResponse kimmcMessageResponse = messageService.createMessage(kimmcMessageRequest);
			System.out.println("김민철 답장 작성: " + (kimmcMessageResponse != null ? "성공" : "실패"));
			if (kimmcMessageResponse != null) {
				System.out.println("저장된 메시지 내용: \"" + kimmcMessageResponse.getText() + "\" - (성공)");
			}
			System.out.println("---------------------------------");

			System.out.println("7. 메시지 조회");
			GetMessagesByChannelIdRequest getMessagesRequest = GetMessagesByChannelIdRequest.builder()
				.channelId(channelResponse.getId())
				.build();

			List<MessageResponse> messages = messageService.getAllByChannelId(getMessagesRequest);

			System.out.println("프라이빗 채널의 메시지 목록:");
			for (int i = 0; i < messages.size(); i++) {
				MessageResponse msg = messages.get(i);
				String authorName = msg.getAuthorId().equals(kimytResponse.getId()) ? "김영태" : "김민철";
				System.out.println("[" + (i+1) + "] " + authorName + ": " + msg.getText());
			}
			System.out.println("메시지 조회: " + (messages.size() == 2 ? "성공" : "실패"));
			System.out.println("---------------------------------");

			System.out.println("8. 채널 접근 권한 테스트");

			GetChannelsByUserRequest kimytChannelsRequest = GetChannelsByUserRequest.builder()
				.userId(kimytResponse.getId())
				.build();
			List<ChannelResponse> kimytChannels = channelService.getChannelsByUserId(kimytChannelsRequest);
			System.out.println("김영태가 볼 수 있는 채널 수: " + kimytChannels.size());

			GetChannelsByUserRequest kimmcChannelsRequest = GetChannelsByUserRequest.builder()
				.userId(kimmcResponse.getId())
				.build();
			List<ChannelResponse> kimmcChannels = channelService.getChannelsByUserId(kimmcChannelsRequest);
			System.out.println("김민철이 볼 수 있는 채널 수: " + kimmcChannels.size());

			GetChannelsByUserRequest leedjChannelsRequest = GetChannelsByUserRequest.builder()
				.userId(leedjResponse.getId())
				.build();
			List<ChannelResponse> leedjChannels = channelService.getChannelsByUserId(leedjChannelsRequest);
			System.out.println("이동재가 볼 수 있는 채널 수: " + leedjChannels.size());

			boolean kimytHasAccess = kimytChannels.stream().anyMatch(ch -> ch.getId().equals(channelResponse.getId()));
			boolean kimmcHasAccess = kimmcChannels.stream().anyMatch(ch -> ch.getId().equals(channelResponse.getId()));
			boolean leedjHasAccess = leedjChannels.stream().anyMatch(ch -> ch.getId().equals(channelResponse.getId()));

			System.out.println("김영태 프라이빗 채널 접근: " + (kimytHasAccess ? "가능" : "불가능"));
			System.out.println("김민철 프라이빗 채널 접근: " + (kimmcHasAccess ? "가능" : "불가능"));
			System.out.println("이동재 프라이빗 채널 접근: " + (!leedjHasAccess ? "불가능(정상)" : "가능(오류)"));

			boolean accessControlWorking = kimytHasAccess && kimmcHasAccess && !leedjHasAccess;
			System.out.println("접근 권한 제어: " + (accessControlWorking ? "정상" : "오류"));
			System.out.println("---------------------------------");

			System.out.println("프라이빗 채널 테스트 완료");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}