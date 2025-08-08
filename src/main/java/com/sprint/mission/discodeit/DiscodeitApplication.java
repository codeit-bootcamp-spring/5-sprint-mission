package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
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

import java.util.List;
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

		// 1. USER
		userCrudTest(userService);

		// 2~7 테스트는 필요 시 순서대로 호출
		channelCrudTest(channelService, userService);
		// messageCrudTest(messageService, channelService, userService);
		// readStatusTest(readStatusService, userService, channelService);
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

}
