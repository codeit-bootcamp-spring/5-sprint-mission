package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.deventity.DevUser;
import com.sprint.mission.discodeit.domain.enums.user.Status;
import com.sprint.mission.discodeit.service.dev.DevFriendRequestService;
import com.sprint.mission.discodeit.service.dev.DevUserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(AppProperties.class)
public class DiscodeitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Bean
    @Profile({"test", "dev"})
    ApplicationRunner userServiceRunner(DevUserService userService) {
        return args -> {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            String email = "test+" + suffix + "@example.com";
            String username = "tester_" + suffix;
            String password = "StrongP@ssw0rd!";
            DevUser u = userService.register(
                    email,
                    username,
                    password,
                    LocalDate.now().minusYears(20),
                    true,
                    "테스터"
            );
            System.out.println("회원가입 완료: " + u);

            userService.login(email, password);
            System.out.println("로그인 완료: " + u);

            String newEmail = "test+" + suffix + "-new@example.com";
            String newPassword = "An0therP@ss!" + suffix.substring(0, 2);
            UUID userId = u.getId();
            userService.updateEmail(userId, newEmail);
            userService.updateGlobalName(userId, "글로벌_" + suffix);
            userService.updateUsername(userId, "tester2_" + suffix);
            userService.updatePassword(userId, newPassword);
            userService.updateBirthDate(userId, LocalDate.of(1990, 1, 1));
            userService.updateSubscribedToNewsletter(userId, false);
            userService.updatePhoneNumber(userId, "010-1234-5678");
            userService.updateStatus(userId, Status.IDLE);
            userService.updateAvatar(userId, "https://example.com/avatar.png");
            userService.updateBio(userId, "소개 " + suffix);
            userService.updateVerified(userId, true);
            userService.updateBanned(userId, false);
            System.out.println("프로필 업데이트 완료: " + userId);

            userService.logout(userId);
            System.out.println("로그아웃 완료: " + userId);

            userService.login(newEmail, newPassword);
            System.out.println("재로그인 완료: " + userId);

            String email2 = "friend+" + suffix + "@example.com";
            String username2 = "friend_" + suffix;
            String password2 = "Fr1endP@ss!";
            DevUser u2 = userService.register(
                    email2,
                    username2,
                    password2,
                    LocalDate.now().minusYears(22),
                    false,
                    "친구"
            );
            UUID userId2 = u2.getId();
            userService.addFriend(userId, userId2);
            System.out.println("친구 추가 후 수: " + userService.getFriends(userId).size());
            userService.removeFriend(userId, userId2);
            System.out.println("친구 제거 후 수: " + userService.getFriends(userId).size());

            UUID guildId = UUID.fromString("2cad452a-c0f2-4227-87a3-5b53d2340b20");
            userService.joinGuild(userId, guildId);
            System.out.println("길드 참가 후 수: " + userService.getGuilds(userId).size());
            userService.leaveGuild(userId, guildId);
            System.out.println("길드 탈퇴 후 수: " + userService.getGuilds(userId).size());

            UUID chatRoomId = UUID.randomUUID();
            userService.joinChatRoom(userId, chatRoomId);
            userService.leaveChatRoom(userId, chatRoomId);
            System.out.println("채팅방 참가/탈퇴 완료");

            userService.deactivateAccount(userId);
            userService.reactivateAccount(userId);
            userService.deleteAccount(userId2);
            System.out.println("계정 삭제 완료: " + userId2);

            userService.logout(userId);
            System.out.println("로그아웃 완료: " + userId);

            userService.hardDeleteAccount(userId);
            userService.hardDeleteAccount(userId2);
        };
    }

    @Bean
    @Profile({"test", "dev"})
    ApplicationRunner friendRequestService(DevFriendRequestService friendRequestService,
                                           DevUserService userService) {
        return args -> {
            String sfx = UUID.randomUUID().toString().substring(0, 6);

            DevUser userA = userService.register(
                    "alice+" + sfx + "@example.com", "alice_" + sfx, "Al1ceP@ss!",
                    LocalDate.now().minusYears(21), true, "앨리스");
            DevUser userB = userService.register(
                    "bob+" + sfx + "@example.com", "bob_" + sfx, "B0bP@ss!",
                    LocalDate.now().minusYears(22), false, "밥");

            UUID a = userA.getId();
            UUID b = userB.getId();

            System.out.println("초기 보낸요청(A): " + friendRequestService.listSent(a).size());
            System.out.println("초기 받은요청(B): " + friendRequestService.listReceived(b).size());

            final UUID requestId = friendRequestService.send(a, b).getId();
            System.out.println("요청 보낸 후 보낸요청(A): " + friendRequestService.listSent(a).size());
            System.out.println("요청 보낸 후 받은요청(B): " + friendRequestService.listReceived(b).size());

            try {
                friendRequestService.send(a, b);
                System.out.println("중복 요청이 허용되면 안 됩니다.");
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("중복 요청 차단 확인: " + e.getMessage());
            }

            friendRequestService.accept(requestId);
            System.out.println("수락 후 보낸요청(A): " + friendRequestService.listSent(a).size());
            System.out.println("수락 후 받은요청(B): " + friendRequestService.listReceived(b).size());
            System.out.println("수락 후 친구수(A): " + userService.getFriends(a).size());
            System.out.println("수락 후 친구수(B): " + userService.getFriends(b).size());

            DevUser userC = userService.register(
                    "charlie+" + sfx + "@example.com", "charlie_" + sfx, "Ch@rl1eP@ss!",
                    LocalDate.now().minusYears(23), false, "찰리");

            UUID c = userC.getId();

            final UUID requestId2 = friendRequestService.send(b, c).getId();
            System.out.println("B→C 보낸요청(B): " + friendRequestService.listSent(b).size());
            System.out.println("B→C 받은요청(C): " + friendRequestService.listReceived(c).size());

            friendRequestService.reject(requestId2);
            System.out.println("거절 후 보낸요청(B): " + friendRequestService.listSent(b).size());
            System.out.println("거절 후 받은요청(C): " + friendRequestService.listReceived(c).size());

            try {
                friendRequestService.clear(a);
                System.out.println("A의 대기 요청 정리 완료");
            } catch (UnsupportedOperationException ignored) {
                throw new Exception();
            }

            userService.hardDeleteAccount(a);
            userService.hardDeleteAccount(b);
            userService.hardDeleteAccount(c);
        };
    }

}
