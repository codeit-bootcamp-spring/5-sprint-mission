package main.java;

import java.util.List;
import java.util.Scanner;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class BasicTest {
	private final Scanner sc = new Scanner(System.in);
	// private final JCFUserService userService = new JCFUserService();
	// private final JCFChannelService channelService = new JCFChannelService();
	// private final JCFMessageService messageService = new JCFMessageService(userService, channelService);

	private final FileUserService userService = new FileUserService();
	private final FileChannelService channelService = new FileChannelService();
	private final FileMessageService messageService = new FileMessageService(userService, channelService);

	public void run() {
		System.out.println("=====유저 서비스 테스트=====");

		userService.createUser("test1", "pw123", "홍길동");
		userService.createUser("test2", "pw456", "김길동");
		userService.createUser("test3", "pw789", "박세준");

		// 생성 체크
		User userDummy = userService.getUser("test1");
		System.out.println(userDummy.getLoginId() + " (" + userDummy.getDefaultNickname() + ") - 생성 성공");
		userService.getUser("test2");
		userService.getUser("test3");

		// 중복 체크
		User duplicateUser = userService.createUser("test1", "password123", "형을형이라부르지못하는자");
		System.out.println("중복 사용자 생성 결과: " + (duplicateUser == null ? "테스트 성공" : "테스트 실패"));

		System.out.println("입력 생성 체크");

		System.out.print("로그인 ID를 입력하세요: ");
		String loginId = sc.nextLine();
		System.out.print("비밀번호를 입력하세요: ");
		String password = sc.nextLine();
		System.out.print("닉네임을 입력하세요: ");
		String nickname = sc.nextLine();
		User newUser = userService.createUser(loginId, password, nickname);
		userDummy = userService.login(loginId, password);
		if (userDummy == null) {
			System.out.println("로그인 실패");
			System.out.println("test1/pw123 으로 로그인 설정");

			loginId = "test1";
			password = "pw123";
		} else {
			System.out.println("ID: " + userDummy.getId());
			System.out.println("로그인 ID: " + userDummy.getLoginId());
			System.out.println("닉네임: " + userDummy.getDefaultNickname());
			System.out.println("생성 시간: " + userDummy.getCreatedAt());
		}

		System.out.println("로그인 테스트");
		User loginSuccess = userService.login("test1", "pw123");
		User loginFail = userService.login("test2", "12125gdf46");
		System.out.println("올바른 비밀번호 로그인: " + (loginSuccess != null ? "성공" : "실패"));
		System.out.println("잘못된 비밀번호 로그인: " + (loginFail == null ? "성공" : "실패"));

		System.out.println("조회 테스트");
		userDummy = userService.getUser(loginId);
		System.out.println("닉네임: " + userDummy.getDefaultNickname());

		boolean updateResult = userService.updateUserPassword(userDummy.getId(), "newpassword123");
		System.out.println("비밀번호 변경: " + (updateResult ? "성공" : "실패"));

		User loginWithNewPass = userService.login(userDummy.getLoginId(), "newpassword123");
		System.out.println("새 비밀번호로 로그인: " + (loginWithNewPass != null ? "성공" : "실패"));

		System.out.println("전체 조회");
		List<User> allUsers = userService.getUserAll();
		for (int i = 0; i < allUsers.size(); i++) {
			User user = allUsers.get(i);
			System.out.println((i + 1) + ". " + user.getDefaultNickname() + " (" + user.getLoginId() + ")");
		}

		System.out.println("=====채널 서비스 테스트=====");

		System.out.println("\n채널 생성 테스트\n");
		channelService.createChannel("일반");
		channelService.createChannel("개발팀");
		channelService.createChannel("취미");

		List<Channel> allChannels = channelService.findChannelAll();
		for (int i = 0; i < allChannels.size(); i++) {
			Channel channel = allChannels.get(i);
			System.out.println((i + 1) + ". " + channel.getChannelName() +
				" (멤버 수: " + channel.getChannelUsersUUID().size() + ", " +
				"메시지 수: " + channel.getChannelMessagesUUID().size() + "개)");
		}

		Channel duplicateChannel = channelService.createChannel("일반");
		System.out.println("중복 채널 생성: " + (duplicateChannel == null ? "성공" : "실패"));

		System.out.println("채널 참가 테스트");
		userDummy = userService.getUser("test1");
		channelService.joinChannel(userDummy, "일반");
		userDummy = userService.getUser("test1");
		channelService.joinChannel(userDummy, "개발팀");
		userDummy = userService.getUser("test2");
		System.out.println("일반 채널 참가: " + (channelService.joinChannel(userDummy, "일반") ? "성공" : "실패"));
		userDummy = userService.getUser("test3");
		channelService.joinChannel(userDummy, "취미");

		System.out.println("존재하지 않는 채널 참가 시도: " + (channelService.joinChannel(userDummy, "존재하지않는채널") ? "실패" : "성공"));

		Channel generalChannel = channelService.findChannel("일반");
		System.out.println("채널명 조회, 채널명: " + generalChannel.getChannelName());

		allChannels = channelService.findChannelAll();
		for (int i = 0; i < allChannels.size(); i++) {
			Channel channel = allChannels.get(i);
			System.out.println((i + 1) + ". " + channel.getChannelName() +
				" (멤버 수: " + channel.getChannelUsersUUID().size() + ", " +
				"메시지 수: " + channel.getChannelMessagesUUID().size() + ")");
		}

		System.out.println("====메시지 서비스 테스트=====");

		userDummy = userService.getUser("test1");
		generalChannel = channelService.findChannel("일반");

		System.out.println("메시지 1 생성: "
			+ (messageService.createMessage(userDummy.getId(),
			generalChannel.getId(),
			"안녕하세요! 처음 왔어요.") ? "성공" : "실패"));

		userDummy = userService.getUser("test2");
		System.out.println("메시지 2 생성: "
			+ (messageService.createMessage(userDummy.getId(),
			generalChannel.getId(),
			"환영합니다!") ? "성공" : "실패"));

		userDummy = userService.getUser("test1");

		System.out.println("메시지 2 생성: "
			+ (messageService.createMessage(userDummy.getId(),
			generalChannel.getId(),
			"ㄳㄳ") ? "성공" : "실패"));

		System.out.println("비참가자 메시지 생성 시도");

		userDummy = userService.getUser("test3");
		System.out.println("메시지 2 생성: "
			+ (messageService.createMessage(userDummy.getId(),
			generalChannel.getId(),
			"? 친목 금지여") ? "성공" : "실패"));

		List<Message> channelMessages = messageService.getMessageByChannel(generalChannel.getId());

		System.out.println("일반 채널 메시지 수: " + channelMessages.size());

		for (int i = 0; i < Math.min(5, channelMessages.size()); i++) {
			Message msg = channelMessages.get(i);
			User author = userService.getUser(msg.getAuthorUUID());
			String authorNick = generalChannel.getUserNickname(author.getId());
			if (authorNick == null) authorNick = author.getDefaultNickname();

			System.out.println((i + 1) + ". " + authorNick + ": " + msg.getText());
		}

		// 작성자별 메시지 조회
		System.out.println("\n작성자별 메시지 조회:");
		List<String> nicknames = channelService.findChannelMemberNickname("일반");
		for (String nn : nicknames) {
			List<Message> authorMessages = messageService.getMessageByAuthor(nn, generalChannel.getId());
			System.out.println(nn + "님이 작성한 메시지: " + authorMessages.size() + "개");
		}

		Message firstMsg = channelMessages.get(0);
		User msgAuthor = userService.getUser(firstMsg.getAuthorUUID());
		String authorNick = generalChannel.getUserNickname(msgAuthor.getId());
		if (authorNick == null) authorNick = msgAuthor.getDefaultNickname();

		System.out.println("수정할 메시지 정보:");
		System.out.println("작성자: " + authorNick);
		System.out.println("원본 내용: " + firstMsg.getText());
		System.out.println("작성 시간: " + firstMsg.getCreatedAt());
		System.out.println("수정 전 업데이트 시간: " + firstMsg.getUpdatedAt());

		System.out.println("\n메시지 수정: " + (messageService.updateMessage(firstMsg.getId(), firstMsg.getAuthorUUID(),
			firstMsg.getText() + " (수정됨)") ? "성공" : "실패"));

		Message updatedMsg = messageService.getMessage(firstMsg.getId());
		System.out.println("수정된 내용: " + updatedMsg.getText());
		System.out.println("수정 후 업데이트 시간: " + updatedMsg.getUpdatedAt());

		System.out.println("일반 채널 메시지 작성자 이름 바꾸기");
		System.out.println(generalChannel.getUserNickname(userService.getUser(firstMsg.getAuthorUUID()).getId()));
		System.out.println(channelService.updateUserNickname(generalChannel.getId(), firstMsg.getAuthorUUID(), "황올먹고싶다"));
		System.out.println(generalChannel.getUserNickname(userService.getUser(firstMsg.getAuthorUUID()).getId()));
		System.out.println();

		System.out.println("메시지 전체 조회 테스트");
		allChannels = channelService.findChannelAll();
		for (Channel channel : allChannels){
			System.out.println("\n채널 명: " + channel.getChannelName() + " (멤버 " + channel.getChannelUsersUUID().size() + "명)");
			List<Message> channelMessages2 = messageService.getMessageByChannel(channel.getId());

			for (int i = 0; i < channelMessages2.size(); i++) {
				Message msg = channelMessages2.get(i);
				User author = userService.getUser(msg.getAuthorUUID());
				authorNick = channel.getUserNickname(author.getId());
				if (authorNick == null) authorNick = author.getDefaultNickname();

				System.out.println("   " + (i + 1) + ". [" + authorNick + "] " + msg.getText() +
					" (작성: " + msg.getCreatedAt() +
					(msg.getUpdatedAt().equals(msg.getCreatedAt()) ? "" : ", 수정 시간: " + msg.getUpdatedAt()) + ")");
			}
		}

		messageService.deleteMessage(updatedMsg.getId(), updatedMsg.getAuthorUUID());

		List<Message> channelMessages3 =messageService.getMessageByAuthor(authorNick, generalChannel.getId());
		for (int i = 0; i < channelMessages3.size(); i++) {
			Message msg = channelMessages3.get(i);
			User author = userService.getUser(msg.getAuthorUUID());
			authorNick = generalChannel.getUserNickname(author.getId());
			if (authorNick == null) authorNick = author.getDefaultNickname();

			System.out.println("   " + (i + 1) + ". [" + authorNick + "] " + msg.getText() +
				" (작성: " + msg.getCreatedAt() +
				(msg.getUpdatedAt().equals(msg.getCreatedAt()) ? "" : ", 수정 시간: " + msg.getUpdatedAt()) + ")");
		}


	}
}