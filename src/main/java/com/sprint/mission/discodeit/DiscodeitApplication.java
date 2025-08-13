package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		userCRUDTest(userService);
		channelCRUDTest(channelService);
		messageCRUDTest(userService, channelService, messageService);
	}

	private static void setupUser(UserService userService) {
		userService.register(new CreateUserRequest("aaa@aaa.aaa", "just_do_it", "홍길동", "1234", "010-1234-5678"), null);
		userService.register(new CreateUserRequest("bbb@bbb.bbb", "what_can_i_do", "홍길동", "1234", "010-2345-1234"), null);
		userService.register(new CreateUserRequest("ccc@ccc.ccc", "anything", "이길동", "1234", "010-3456-5678"), null);
		userService.register(new CreateUserRequest("ddd@ddd.ddd", "omg", "이길동", "1234", "010-1234-1234"), null);
		userService.register(new CreateUserRequest("eee@eee.eee", "this_code", "김길동", "1234", "010-5678-5678"), null);

		System.out.println("user setup end...");
	}

	private static void setupChannel(ChannelService channelService) {
//		channelService.createChannel("소개합니다", "개성 가득한 소개를 남겨주세요.");
//		channelService.createChannel("소통할까요", "다양한 주제로 자유롭게 소통하는 채널이에요.");
//		channelService.createChannel("공지", "이 채널에 공유되는 소식은 꼭 확인해주세요.");
		System.out.println("채널 셋업 완료...");

		System.out.print("------ 모든 채널 확인 -------\n");
//		for (Channel channel : channelService.getAll()) {
//			System.out.print(channel);
//			System.out.print("--------------\n");
//		}
	}

	private static void setupMessage(UserService userService, ChannelService channelService, MessageService messageService) {
//		userService.register("test1@test.com", "send_user", "주건희", "test", "010-1111-2222");
//		userService.register("test2@test.com", "receive_user", "받건희", "test", "010-2222-3333");

//		Channel testChannel1 = channelService.createChannel("메시지 테스트 채널1", "테스트용 채널입니다.");
//		Channel testChannel2 = channelService.createChannel("메시지 테스트 채널2", "테스트용 채널입니다.");

//		User sendUser = userService.getByUserName("send_user");

//		messageService.createMessage(sendUser, testChannel1, "심심하다");
//		messageService.createMessage(sendUser, testChannel1, "나랑 같이 떠들 사람");
//		messageService.createMessage(userService.getByUserName("receive_user"), testChannel2, "들썩 들썩 떠들썩");
		System.out.println("메시지 셋업 완료...");
	}

	private static void userCRUDTest(UserService userService) {
		System.out.print("============User CRUD Test============\n");

		setupUser(userService);

		UserResponse registerTest1 = userService.register(new CreateUserRequest("fff@fff.fff", "Unnamed", "김길동", "1234", "010-5678-5678"), null);
		UserResponse registerTest2 = userService.register(new CreateUserRequest("eee@eee.eee", "Unnamed", "호고동", "5678", "010-5678-5678"), null);
		UserResponse registerTest3 = userService.register(new CreateUserRequest("eee@naver.com", "this_code", "김길동", "1234", "010-5678-5678"), null);

		if (registerTest1 != null) System.out.println("유저 생성 테스트 :: 통과");
		else System.out.println("유저 생성 테스트:: 실패");
		if (registerTest2 == null) System.out.println("유저 생성 테스트(중복 이메일) :: 통과");
		else System.out.println("유저 생성 테스트(중복 이메일) :: 실패");
		if (registerTest3 == null) System.out.println("유저 생성 테스트(중복 유저명) :: 통과");
		else System.out.println("유저 생성 테스트(중복 유저명) :: 실패");

		Optional<UserResponse> findByEmailTest1 = userService.getByEmail("aaa@aaa.aaa");
		Optional<UserResponse> findByEmailTest2 = userService.getByEmail("zzz@zzz.zzz");

		if (findByEmailTest1.isPresent()) System.out.println("유저 검색 테스트(이메일) :: 통과");
		else System.out.println("유저 검색 테스트(이메일) :: 실패");
		if (findByEmailTest2.isEmpty()) System.out.println("유저 검색 테스트(없는 이메일) :: 통과");
		else System.out.println("유저 검색 테스트(없는 이메일) :: 실패");

		Optional<UserResponse> findByUserNameTest1 = userService.getByUserName("omg");
		Optional<UserResponse> findByUserNameTest2 = userService.getByUserName("holy_moly");

		if (findByUserNameTest1.isPresent()) System.out.println("유저 검색 테스트(유저명) :: 통과");
		else System.out.println("유저 검색 테스트(유저명) :: 실패");
		if (findByUserNameTest2.isEmpty()) System.out.println("유저 검색 테스트(없는 유저명) :: 통과");
		else System.out.println("유저 검색 테스트(없는 유저명) :: 실패");

		List<UserResponse> findByNicknameTest1 = userService.searchByNickname("길동");
		List<UserResponse> findByNicknameTest2 = userService.searchByNickname("절대 없는 닉네임");

		if (findByNicknameTest1 != null && !findByNicknameTest1.isEmpty()) System.out.println("유저 검색 테스트(닉네임) :: 통과");
		else System.out.println("유저 검색 테스트(닉네임) :: 실패");
		if (findByNicknameTest2 != null && findByNicknameTest2.isEmpty()) System.out.println("유저 검색 테스트(없는 닉네임) :: 통과");
		else System.out.println("유저 검색 테스트(없는 닉네임) :: 실패");

		UserResponse updateTestUser1 = userService.getByEmail("aaa@aaa.aaa").get();
		UserResponse updateTestUser2 = userService.getByEmail("bbb@bbb.bbb").get();

		UserResponse updateTest1 = userService.update(new UpdateUserRequest(updateTestUser1.id(), "xxx@xxx.xxx", null, null, null, null), null);
		UserResponse updateTest2 = userService.update(new UpdateUserRequest(updateTestUser2.id(), "xxx@xxx.xxx", null, null, null, null), null);

		if (updateTest1 != null) System.out.println("유저 수정 테스트 :: 통과");
		else System.out.println("유저 수정 테스트 :: 실패");
		if (updateTest2 == null) System.out.println("유저 수정 테스트(없는 유저) :: 통과");
		else System.out.println("유저 수정 테스트(없는 유저) :: 실패");

		List<UserResponse> userResponseList = userService.getAll();
		if (userService.remove(userResponseList.get(0).id())) System.out.println("유저 삭제 테스트 :: 통과");
		else System.out.println("유저 삭제 테스트 :: 실패");
		if (!userService.remove(UUID.randomUUID())) System.out.println("유저 삭제 테스트(없는 유저) :: 통과");
		else System.out.println("유저 삭제 테스트(없는 유저) :: 실패");
	}

	private static void channelCRUDTest(ChannelService channelService) {
		System.out.print("============Channel CRUD Test============\n");

		setupChannel(channelService);

//		Channel testChannel = channelService.createChannel("테스트 채널", "테스트 전용 채널입니다");

//		List<Channel> findChannels = channelService.getByChannelName("테스트");
//		if (findChannels == null || findChannels.isEmpty()) System.out.print("채널 검색 테스트 :: 실패");
//		else System.out.println("채널 검색 테스트 :: 성공");

		String channelName = "TEST CHANNEL";
//		channelService.update(testChannel.getId(), channelName, "테스트 전용 채널입니다.");
//		Optional<Channel> optionalChannel = channelService.getByChannelName(channelName).stream()
//				.filter(channel -> channel.getChannelName().equals(channelName))
//				.findFirst();
//		if (optionalChannel.isPresent()) System.out.println("채널 수정 테스트 :: 성공");
//		else System.out.println("채널 수정 테스트 :: 실패");

//		if (channelService.removeById(testChannel.getId())) System.out.println("채널 삭제 테스트 :: 성공");
//		else System.out.println("유저 삭제 테스트 :: 실패");
	}

	private static void messageCRUDTest(UserService userService, ChannelService channelService, MessageService messageService) {
		System.out.print("============Message CRUD Test============\n");

		setupMessage(userService, channelService, messageService);

//		User testUser = userService.getByUserName("send_user");
//		List<Message> messageList = messageService.getByUser(testUser);
//		if (!messageList.isEmpty()) System.out.println("메시지 검색 테스트 :: 성공");
//		else System.out.println("메시지 검색 테스트 :: 실패");
//
//		Channel testChannel = channelService.getByChannelName("메시지 테스트 채널").get(0);
//		Message testMessage = messageService.createMessage(testUser, testChannel, "테스트를 위한 메시지");
//
//		String updateMessage = "아아 메시지 테스트";
//		if (messageService.updateById(testMessage.getId(), testUser, testChannel, testMessage.getMessage(), updateMessage)) {
//			System.out.println("메시지 수정 테스트 :: 성공");
//		} else System.out.println("메시지 수정 테스트 :: 실패");
//
//		if (messageService.removeById(testMessage.getId(), testUser, testChannel)) {
//			System.out.println("메시지 삭제 테스트 :: 성공");
//		} else System.out.println("메시지 삭제 테스트 :: 실패");
	}

}
