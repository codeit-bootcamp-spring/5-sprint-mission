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

		// 필요한 빈 조회 (다른 테스트는 추후 사용)
		UserService userService = ctx.getBean(UserService.class);
		ChannelService channelService = ctx.getBean(ChannelService.class);
		MessageService messageService = ctx.getBean(MessageService.class);
		ReadStatusService readStatusService = ctx.getBean(ReadStatusService.class);
		UserStatusService userStatusService = ctx.getBean(UserStatusService.class);
		BinaryContentService binaryContentSvc = ctx.getBean(BinaryContentService.class);
		AuthService authService = ctx.getBean(AuthService.class);

		userCrudTest(userService);
		channelCrudTest(channelService, userService);
		messageCrudTest(messageService, channelService, userService);
		readStatusTest(readStatusService, userService, channelService);
		// userStatusTest(userStatusService, userService);
		// binaryContentTest(binaryContentSvc);
		// authLoginTest(authService, userService);
	}

	// ================== [1. USER CRUD 테스트] ==================
	private static void userCrudTest(UserService userService) {
		System.out.println("\n=== [USER CRUD 테스트] ===");

		// --- Create ---
		UUID userId = userService.create(
				new UserCreateRequest("김유민", "yumin@example.com", "qwer1234")
		);
		System.out.println("[생성] userId = " + userId);

		// --- Read (성공) ---
		Optional<UserResponse> readOk = userService.read(userId);
		System.out.println("[조회 – 성공] " + readOk.orElse(null));

		// --- Read (실패) ---
		Optional<UserResponse> readFail = userService.read(UUID.randomUUID());
		System.out.println("[조회 – 실패] " + readFail.orElse(null));

		// --- Read All ---
		List<UserResponse> allUsers = userService.readAll();
		System.out.println("[전체 조회] " + allUsers);

		// --- Update (성공) ---
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
		System.out.println("[수정 – 성공] 변경 " + updateOk
				+ ", 결과 = " + userService.read(userId).orElse(null)); // ★ userId로 조회

		// --- Update (실패 기대) ---
		try {
			boolean updateFail = userService.update(
					new UserUpdateRequest(
							UUID.randomUUID(), // 없는 ID
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

		// --- Delete (성공) ---
		boolean deleteOk = false;
		try {
			deleteOk = userService.delete(userId);
		} catch (Exception e) {
			System.out.println("[삭제] 처리 중 예외: " + e.getMessage());
		}
		System.out.println("[삭제 – 성공] 삭제 " + deleteOk
				+ ", 조회 결과 = " + userService.read(userId).orElse(null)); // ★ userId로 조회(삭제 후 null 기대)
	}

	// ================== [2. CHANNEL CRUD 테스트] ==================
	private static void channelCrudTest(ChannelService channelService, UserService userService) {
		System.out.println("\n=== [CHANNEL CRUD 테스트] ===");

		// --- Public 채널 생성 ---
		UUID pubChId = channelService.createPublicChannel(
				new CreateChannelRequest("공지", "전체 공지 채널")
		);
		System.out.println("[생성] publicChannelId = " + pubChId);

		// --- Public 채널 조회 (성공) ---
		System.out.println("[조회 - 성공] " + channelService.find(pubChId).orElse(null));

		// --- Public 채널 조회 (실패: 랜덤 ID) ---
		System.out.println("[조회 - 실패] " + channelService.find(UUID.randomUUID()).orElse(null));

		// --- Private 채널용 멤버 2명 생성 ---
		UUID m1 = userService.create(new UserCreateRequest("member1", "m1@example.com", "pw1"));
		UUID m2 = userService.create(new UserCreateRequest("member2", "m2@example.com", "pw2"));

		// --- Private 채널 생성 ---
		UUID priChId = channelService.createPrivateChannel(
				new PrivateChannelRequest(List.of(m1, m2))
		);
		System.out.println("[생성] privateChannelId = " + priChId);

		// --- 사용자별 채널 전체 조회 ---
		System.out.println("[전체 조회 - member1] " + channelService.findAllByUserId(m1));
		System.out.println("[전체 조회 - 임의사용자] " + channelService.findAllByUserId(UUID.randomUUID()));

		// --- Public 채널 수정 ---
		boolean upd = channelService.update(
				new ChannelUpdateRequest(pubChId, "공지(수정)", "수정된 공지 채널")
		);
		System.out.println("[수정] 변경 " + upd + ", 결과 = " + channelService.find(pubChId).orElse(null));

		// --- 채널 삭제 ---
		boolean delPub = channelService.delete(pubChId);
		boolean delPri = channelService.delete(priChId);
		System.out.println("[삭제] public=" + delPub + ", private=" + delPri
				+ " / 조회 결과 = " + channelService.find(pubChId).orElse(null));
	}

	// ================== [3. MESSAGE CRUD 테스트] ==================
	private static void messageCrudTest(
			MessageService messageService,
			ChannelService channelService,
			UserService userService
	) {
		System.out.println("\n=== [MESSAGE CRUD 테스트] ===");

		// 준비: 유저 + 채널 하나씩
		UUID userId = userService.create(new UserCreateRequest("msgUser", "msg@example.com", "pw"));
		UUID chId   = channelService.createPublicChannel(new CreateChannelRequest("공지", "전체 공지 채널", null));

		// --- Create ---
		UUID messageId = messageService.create(
				new MessageCreateRequest("안녕하세요!", chId, userId)
		);
		System.out.println("[생성] messageId = " + messageId);

		// --- Read (성공) ---
		var readOk = messageService.find(messageId);
		System.out.println("[조회 - 성공] " + readOk.orElse(null));

		// --- Read (실패) ---
		var readFail = messageService.find(UUID.randomUUID());
		System.out.println("[조회 - 실패] " + readFail.orElse(null));

		// --- Read All by Channel ---
		var byChannel = messageService.findAllByChannelId(chId);
		System.out.println("[채널별 전체 조회] size=" + byChannel.size());
		byChannel.forEach(System.out::println);

		// --- Update (성공) ---
		boolean updateOk = messageService.update(
				new MessageUpdateRequest(messageId, "수정된 메시지 내용입니다.")
		);
		System.out.println("[수정] 변경 " + updateOk + ", 결과 = " + messageService.find(messageId).orElse(null));

		// --- Update (실패) ---
		try {
			boolean updateFail = messageService.update(
					new MessageUpdateRequest(UUID.randomUUID(), "없는 메시지 수정")
			);
			System.out.println("[수정 - 실패] 변경 " + updateFail); // 보통 false 이거나 예외
		} catch (Exception e) {
			System.out.println("[수정 - 실패] 예외 OK: " + e.getMessage());
		}

		// --- Delete (성공) ---
		boolean deleteOk = messageService.delete(messageId);
		System.out.println("[삭제] 삭제 " + deleteOk + " / 조회 결과 = " + messageService.find(messageId).orElse(null));
	}

	// ================== [4. READ STATUS 테스트] ==================
	private static void readStatusTest(
			ReadStatusService readStatusService,
			UserService userService,
			ChannelService channelService
	) {
		System.out.println("\n=== [READ STATUS 테스트] ===");

		// 준비: 전용 유저/채널 생성
		UUID rsUser = userService.create(new UserCreateRequest("RS-User", "rs@example.com", "pass"));
		UUID rsChannel = channelService.createPublicChannel(
				new CreateChannelRequest("RS-Channel", "ReadStatus 전용 채널", null)
		);

		// --- Create ---
		ReadStatus rsId = readStatusService.create(
				// DTO 파라미터 순서가 (userId, channelId, lastReadAt) 이거나 (channelId, userId, lastReadAt)일 수 있습니다.
				// 컴파일 오류 나면 userId/channelId 자리만 바꿔 주세요!
				new ReadStatusCreateRequest(rsUser, rsChannel, Instant.now())
		);
		System.out.println("[생성] readStatusId = " + rsId);

		// --- Read (성공) ---
		java.util.Optional<ReadStatusResponse> readOk = readStatusService.find(rsId);
		System.out.println("[조회 - 성공] " + readOk.orElse(null));

		// --- Read (실패) ---
		java.util.Optional<ReadStatusResponse> readFail = readStatusService.find(java.util.UUID.randomUUID());
		System.out.println("[조회 - 실패] " + readFail.orElse(null));

		// --- List by Channel ---
		java.util.List<ReadStatusResponse> byChannel = readStatusService.findAllByChannelId(rsChannel);
		System.out.println("[채널 기준 전체 조회] size=" + byChannel.size() + " -> " + byChannel);

		// --- List by User ---
		List<ReadStatus> byUser = readStatusService.findAllByUserId(rsUser);
		System.out.println("[유저 기준 전체 조회] size=" + byUser.size() + " -> " + byUser);

		// --- Update (성공) ---
		ReadStatus updOk = readStatusService.update(
				new ReadStatusUpdateRequest(rsId, Instant.now().plusSeconds(30))
		);
		System.out.println("[수정 - 성공] 변경 " + updOk + ", 결과 = " + readStatusService.find(rsId).orElse(null));

		// --- Update (실패) ---
		ReadStatus updFail = readStatusService.update(
				new ReadStatusUpdateRequest(UUID.randomUUID(), Instant.now())
		);
		System.out.println("[수정 - 실패] 변경 " + updFail);

		// --- Duplicate Create (선택) ---
		try {
			readStatusService.create(new ReadStatusCreateRequest(rsUser, rsChannel, java.time.Instant.now()));
			System.out.println("[중복 생성] 허용됨(서비스 정책 확인 필요)");
		} catch (IllegalArgumentException dup) {
			System.out.println("[중복 생성] 예외 OK: " + dup.getMessage());
		}

		// --- Delete ---
		boolean delOk = readStatusService.delete(rsId);
		System.out.println("[삭제] 삭제 " + delOk + " / 조회 결과 = " + readStatusService.find(rsId).orElse(null));
	}


}
