package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(DiscodeitApplication.class, args);

		// 테스트에 필요한 빈 주입 (나머지는 필요 시 사용)
		UserService userService = ctx.getBean(UserService.class);
		ChannelService channelService = ctx.getBean(ChannelService.class);
		MessageService messageService = ctx.getBean(MessageService.class);
		ReadStatusService readStatusService = ctx.getBean(ReadStatusService.class);
		UserStatusService userStatusService = ctx.getBean(UserStatusService.class);
		BinaryContentService binaryContentSvc = ctx.getBean(BinaryContentService.class);

		userCrudTest(userService);
		channelCrudTest(channelService, userService);
		messageCrudTest(messageService, channelService, userService);
		readStatusTest(readStatusService, userService, channelService);
		// userStatusTest(userStatusService, userService); // 아직 구현 안 함
		// binaryContentTest(binaryContentSvc);           // 아직 구현 안 함
	}

	// ================== [1. 사용자(User) CRUD 테스트] ==================
	private static void userCrudTest(UserService userService) {
		System.out.println("\n=== [USER CRUD 테스트] ===");

		// 생성
		UUID userId = userService.create(
				new UserCreateRequest("김유민", "yumin@example.com", "qwer1234")
		);
		System.out.println("[생성] userId = " + userId);

		// 조회(성공)
		Optional<UserResponse> readOk = userService.read(userId);
		System.out.println("[조회 – 성공] " + readOk.orElse(null));

		// 조회(실패)
		Optional<UserResponse> readFail = userService.read(UUID.randomUUID());
		System.out.println("[조회 – 실패] " + readFail.orElse(null));

		// 전체 조회
		List<UserResponse> allUsers = userService.readAll();
		System.out.println("[전체 조회] " + allUsers);

		// 수정(성공)
		boolean updateOk = false;
		try {
			updateOk = userService.update(
					new UserUpdateRequest(
							userId,
							"김유민(수정)",
							"yumin2@example.com",
							null,
							null
					)
			);
		} catch (Exception e) {
			System.out.println("[수정 – 성공] 처리 중 예외: " + e.getMessage());
		}
		// 수정 후 같은 userId로 재조회
		System.out.println("[수정 – 성공] 변경 " + updateOk
				+ ", 결과 = " + userService.read(userId).orElse(null));

		// 수정(실패): 존재하지 않는 ID
		try {
			boolean updateFail = userService.update(
					new UserUpdateRequest(
							UUID.randomUUID(),
							"없음",
							null,
							null,
							null
					)
			);
			System.out.println("[수정 – 실패 예상] 실제 변경 = " + updateFail);
		} catch (Exception e) {
			System.out.println("[수정 – 실패] 예외 OK: " + e.getMessage());
		}

		// 삭제(성공)
		boolean deleteOk = false;
		try {
			deleteOk = userService.delete(userId);
		} catch (Exception e) {
			System.out.println("[삭제] 처리 중 예외: " + e.getMessage());
		}
		// 삭제 후 같은 userId로 재조회
		System.out.println("[삭제 – 성공] 삭제 " + deleteOk
				+ ", 조회 결과 = " + userService.read(userId).orElse(null));
	}

	// ================== [2. 채널(Channel) CRUD 테스트] ==================
	private static void channelCrudTest(ChannelService channelService, UserService userService) {
		System.out.println("\n=== [CHANNEL CRUD 테스트] ===");

		// 공용(PUBLIC) 채널 생성
		UUID pubChId = channelService.createPublicChannel(
				new CreateChannelRequest("공지", "전체 공지 채널")
		);
		System.out.println("[생성] publicChannelId = " + pubChId);

		// 공용 채널 조회(성공)
		System.out.println("[조회 - 성공] " + channelService.find(pubChId).orElse(null));

		// 공용 채널 조회(실패)
		System.out.println("[조회 - 실패] " + channelService.find(UUID.randomUUID()).orElse(null));

		// 비공개 채널용 멤버 2명 생성
		UUID m1 = userService.create(new UserCreateRequest("member1", "m1@example.com", "pw1"));
		UUID m2 = userService.create(new UserCreateRequest("member2", "m2@example.com", "pw2"));

		// 비공개 채널 생성
		UUID priChId = channelService.createPrivateChannel(
				new PrivateChannelRequest(List.of(m1, m2))
		);
		System.out.println("[생성] privateChannelId = " + priChId);

		// 사용자 기준 채널 전체 조회
		System.out.println("[전체 조회 - member1] " + channelService.findAllByUserId(m1));
		System.out.println("[전체 조회 - 임의사용자] " + channelService.findAllByUserId(UUID.randomUUID()));

		// 공용 채널 수정
		boolean upd = channelService.update(
				new ChannelUpdateRequest(pubChId, "과제(수정)", "수정된 과제 채널")
		);
		System.out.println("[수정] 변경 " + upd + ", 결과 = " + channelService.find(pubChId).orElse(null));

		// 채널 삭제
		boolean delPub = channelService.delete(pubChId);
		boolean delPri = channelService.delete(priChId);
		System.out.println("[삭제] public=" + delPub + ", private=" + delPri
				+ " / 조회 결과 = " + channelService.find(pubChId).orElse(null));
	}

	// ================== [3. 메시지(Message) CRUD 테스트] ==================
	private static void messageCrudTest(
			MessageService messageService,
			ChannelService channelService,
			UserService userService
	) {
		System.out.println("\n=== [MESSAGE CRUD 테스트] ===");

		// 테스트용 사용자/채널 생성
		UUID userId = userService.create(new UserCreateRequest("msgUser", "msg@example.com", "pw"));
		UUID chId   = channelService.createPublicChannel(new CreateChannelRequest("과제", "전체 과제 채널", null));

		// 생성
		UUID messageId = messageService.create(
				new MessageCreateRequest("안녕하세요!", chId, userId)
		);
		System.out.println("[생성] messageId = " + messageId);

		// 조회(성공)
		var readOk = messageService.find(messageId);
		System.out.println("[조회 - 성공] " + readOk.orElse(null));

		// 조회(실패)
		var readFail = messageService.find(UUID.randomUUID());
		System.out.println("[조회 - 실패] " + readFail.orElse(null));

		// 채널별 전체 조회
		var byChannel = messageService.findAllByChannelId(chId);
		System.out.println("[채널별 전체 조회] size=" + byChannel.size());
		byChannel.forEach(System.out::println);

		// 수정(성공)
		boolean updateOk = messageService.update(
				new MessageUpdateRequest(messageId, "수정된 메시지 내용입니다!")
		);
		System.out.println("[수정] 변경 " + updateOk + ", 결과 = " + messageService.find(messageId).orElse(null));

		// 수정(실패)
		try {
			boolean updateFail = messageService.update(
					new MessageUpdateRequest(UUID.randomUUID(), "없는 메시지 수정")
			);
			System.out.println("[수정 - 실패] 변경 " + updateFail); // 보통 false 또는 예외
		} catch (Exception e) {
			System.out.println("[수정 - 실패] 예외 OK: " + e.getMessage());
		}

		// 삭제(성공)
		boolean deleteOk = messageService.delete(messageId);
		System.out.println("[삭제] 삭제 " + deleteOk + " / 조회 결과 = " + messageService.find(messageId).orElse(null));
	}

	// ================== [4. 읽음상태(ReadStatus) 테스트] ==================
	private static void readStatusTest(
			ReadStatusService readStatusService,
			UserService userService,
			ChannelService channelService
	) {
		System.out.println("\n=== [READ STATUS 테스트] ===");

		// 전용 사용자/채널 생성
		UUID rsUser = userService.create(new UserCreateRequest("RS-User", "rs@example.com", "pass"));
		UUID rsChannel = channelService.createPublicChannel(
				new CreateChannelRequest("RS-Channel", "읽기 전용 채널", null)
		);

		// 생성
		UUID rsId = readStatusService.create(
				new ReadStatusCreateRequest(rsUser, rsChannel, Instant.now())
		);
		System.out.println("[생성] readStatusId = " + rsId);

		// 조회(성공)
		Optional<ReadStatusResponse> readOk = readStatusService.find(rsId);
		System.out.println("[조회 - 성공] " + readOk.orElse(null));

		// 조회(실패)
		Optional<ReadStatusResponse> readFail = readStatusService.find(UUID.randomUUID());
		System.out.println("[조회 - 실패] " + readFail.orElse(null));

		// 채널 기준 전체 조회
		List<ReadStatusResponse> byChannel = readStatusService.findAllByChannelId(rsChannel);
		System.out.println("[채널 기준 전체 조회] size=" + byChannel.size() + " -> " + byChannel);

		// 유저 기준 전체 조회
		List<ReadStatusResponse> byUser = readStatusService.findAllByUserId(rsUser);
		System.out.println("[유저 기준 전체 조회] size=" + byUser.size() + " -> " + byUser);

		// 수정(성공)
		boolean updOk = readStatusService.update(
				new ReadStatusUpdateRequest(rsId, Instant.now().plusSeconds(30))
		);
		System.out.println("[수정 - 성공] 변경 " + updOk + ", 결과 = " + readStatusService.find(rsId).orElse(null));

		// 수정(실패)
		try {
			readStatusService.update(new ReadStatusUpdateRequest(UUID.randomUUID(), Instant.now()));
			System.out.println("[수정 - 실패] 변경");
		} catch (NoSuchElementException e) {
			System.out.println("[수정 - 실패] 예외 : " + e.getMessage());
		}
		// 중복 생성
		try {
			readStatusService.create(new ReadStatusCreateRequest(rsUser, rsChannel, Instant.now()));
			System.out.println("[중복 생성] 허용됨");
		} catch (IllegalStateException e) {
			System.out.println("[중복 생성] 예외 OK: " + e.getMessage());
		}
		// 삭제
		boolean delOk = readStatusService.delete(rsId);
		System.out.println("[삭제] 삭제 " + delOk + " / 조회 결과 = " + readStatusService.find(rsId).orElse(null));
	}
}
