package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.file.FileMessageService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBear(UserService.class);
	}

	// ------------------ [User Test] ------------------
	private static void userCRUDTest(UserService userService) {
		System.out.println("=== [USER 테스트] ===");
		User user = new User("김유민");
		userService.create(user);
		System.out.println("[생성] " + user);

		// 단건 조회 - 성공
		User found = userService.read(user.getId());
		System.out.println("[조회 - 성공] " + found);

		// 단건 조회 - 실패
		User notFound = userService.read(UUID.randomUUID());
		System.out.println("[조회 - 실패] → " + notFound);

		// 전체 조회
		System.out.println("[전체 조회]");
		userService.readAll().forEach(System.out::println);

		// 수정
		boolean updated = userService.update(user.getId(), "김유민(수정)");
		System.out.println("[수정 - 성공] 변경: " + updated);
		System.out.println("[수정 결과] " + userService.read(user.getId()));

		boolean failUpdate = userService.update(UUID.randomUUID(), "없는 이름");
		System.out.println("[수정 - 실패] → 변경: " + failUpdate);

		// 삭제
		userService.delete(user.getId());
		System.out.println("[삭제 - 성공] " + user.getId());
		System.out.println("[삭제 후 조회] " + userService.read(user.getId()));
	}

	// ------------------ [Channel Test] ------------------
	private static void channelCRUDTest(ChannelService channelService) {
		System.out.println("\n=== [CHANNEL 테스트] ===");
		Channel channel = new Channel("미션채널", "스프린트 미션을 진행하는 채널입니다", "text", true);
		channelService.create(channel);
		System.out.println("[생성] " + channel);

		// 단건 조회 - 성공
		Channel found = channelService.findById(channel.getId());
		System.out.println("[조회 - 성공] " + found);

		// 단건 조회 - 실패
		Channel notFound = channelService.findById(UUID.randomUUID());
		System.out.println("[조회 - 실패] → " + notFound);

		// 전체 조회
		System.out.println("[전체 조회]");
		channelService.findAll().forEach(System.out::println);

		// 수정
		Channel updatedChannel = new Channel(channel.getId(), channel.getCreatedAt(), System.currentTimeMillis(), "변경된채널", "바뀐설명", "text", false);
		boolean updated = channelService.update(channel.getId(), updatedChannel);
		System.out.println("[수정 - 성공] 변경: " + updated);
		System.out.println("[수정 결과] " + channelService.findById(channel.getId()));
		boolean failUpdate = channelService.update(UUID.randomUUID(), updatedChannel);
		System.out.println("[수정 - 실패] → 변경: " + failUpdate);

		// 삭제
		channelService.delete(channel.getId());
		System.out.println("[삭제 - 성공] " + channel.getId());
		System.out.println("[삭제 후 조회] " + channelService.findById(channel.getId()));
	}
	// ------------------ [Message Test] ------------------
	private static void messageCRUDTest(MessageService messageService) {
		System.out.println("\n=== [MESSAGE 테스트] ===");

		// 생성
		Message msg = new Message(UUID.randomUUID(), UUID.randomUUID(), "반갑습니다!!", System.currentTimeMillis());
		messageService.create(msg);
		System.out.println("[생성] " + msg);

		// 단건 조회
		Message found = messageService.read(msg.getId());
		System.out.println("[조회 - 성공] " + found);

		// 실패 조회
		Message notFound = messageService.read(UUID.randomUUID());
		System.out.println("[조회 - 실패] → " + notFound);

		// 전체 조회
		System.out.println("[전체 조회]");
		messageService.readAll().forEach(System.out::println);

		// 수정
		boolean successUpdate = messageService.update(msg.getId(), "수정된 메시지");
		System.out.println("[수정 - 성공] 변경: " + successUpdate);
		System.out.println("[수정 결과] " + messageService.read(msg.getId()));

		boolean failUpdate = messageService.update(UUID.randomUUID(), "없는 메시지");
		System.out.println("[수정 - 실패] → 변경: " + failUpdate);

		// 삭제
		messageService.delete(msg.getId());
		System.out.println("[삭제 - 성공] " + msg.getId());
		System.out.println("[삭제 후 조회] " + messageService.read(msg.getId()));
	}

	// 테스트 후 파일 초기화
	// ((FileUserRepository) userRepository).clearFile();
}

}
