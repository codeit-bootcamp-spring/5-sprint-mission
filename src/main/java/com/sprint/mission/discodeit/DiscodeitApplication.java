package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.enums.user.Status;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class DiscodeitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Bean
    @Profile({"test", "dev"})
    ApplicationRunner userServiceRunner(UserService userService) {
        return args -> {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            String email = "test+" + suffix + "@example.com";
            String username = "tester_" + suffix;
            String password = "StrongP@ssw0rd!";
            UUID userId = userService.register(
                    email,
                    username,
                    password,
                    LocalDate.now().minusYears(20),
                    true,
                    "테스터"
            );
            System.out.println("회원가입 완료: " + userId);

            userService.login(email, password);
            System.out.println("로그인 완료: " + userId);

            String newEmail = "test+" + suffix + "-new@example.com";
            String newPassword = "An0therP@ss!" + suffix.substring(0, 2);
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
            UUID userId2 = userService.register(
                    email2,
                    username2,
                    password2,
                    LocalDate.now().minusYears(22),
                    false,
                    "친구"
            );
            userService.addFriend(userId, userId2);
            System.out.println("친구 추가 후 수: " + userService.getFriends(userId).size());
            userService.removeFriend(userId, userId2);
            System.out.println("친구 제거 후 수: " + userService.getFriends(userId).size());

            UUID guildId = UUID.randomUUID();
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
        };
    }
}
