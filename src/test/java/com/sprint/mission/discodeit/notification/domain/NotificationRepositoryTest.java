package com.sprint.mission.discodeit.notification.domain;

import com.sprint.mission.discodeit.global.config.JpaConfig;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("NotificationRepository 슬라이스 테스트")
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User("testuser1", "test1@example.com", "password1234", null));
        user2 = userRepository.save(new User("testuser2", "test2@example.com", "password1234", null));
    }

    @Nested
    @DisplayName("findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc")
    class FindAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc {

        @Test
        @DisplayName("확인되지 않은 알림만 조회")
        void returnsOnlyUncheckedNotifications() {
            // given
            Notification unchecked1 = notificationRepository.save(
                new Notification(user1, "Title 1", "Content 1"));
            Notification unchecked2 = notificationRepository.save(
                new Notification(user1, "Title 2", "Content 2"));
            Notification checked = notificationRepository.save(
                new Notification(user1, "Title 3", "Content 3").check());

            // when
            List<Notification> result = notificationRepository
                .findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(user1.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Notification::getId)
                .containsExactlyInAnyOrder(unchecked1.getId(), unchecked2.getId());
            assertThat(result).extracting(Notification::getId)
                .doesNotContain(checked.getId());
        }

        @Test
        @DisplayName("최신순으로 정렬하여 반환")
        void returnsInDescendingOrderByCreatedAt() throws InterruptedException {
            // given
            Notification older = notificationRepository.save(
                new Notification(user1, "Older", "Content"));
            Thread.sleep(10);
            Notification newer = notificationRepository.save(
                new Notification(user1, "Newer", "Content"));

            // when
            List<Notification> result = notificationRepository
                .findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(user1.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(newer.getId());
            assertThat(result.get(1).getId()).isEqualTo(older.getId());
        }

        @Test
        @DisplayName("다른 사용자의 알림 제외")
        void excludesOtherUserNotifications() {
            // given
            Notification user1Notification = notificationRepository.save(
                new Notification(user1, "User1 Title", "Content"));
            notificationRepository.save(
                new Notification(user2, "User2 Title", "Content"));

            // when
            List<Notification> result = notificationRepository
                .findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(user1.getId());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(user1Notification.getId());
        }

        @Test
        @DisplayName("알림이 없으면 빈 리스트 반환")
        void returnsEmptyList_whenNoNotifications() {
            // when
            List<Notification> result = notificationRepository
                .findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(user1.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("모든 알림이 확인됐으면 빈 리스트 반환")
        void returnsEmptyList_whenAllNotificationsChecked() {
            // given
            notificationRepository.save(new Notification(user1, "Title 1", "Content 1").check());
            notificationRepository.save(new Notification(user1, "Title 2", "Content 2").check());

            // when
            List<Notification> result = notificationRepository
                .findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(user1.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteAllByReceiverId")
    class DeleteAllByReceiverId {

        @Test
        @DisplayName("사용자의 모든 알림 삭제")
        void deletesAllNotificationsForUser() {
            // given
            notificationRepository.save(new Notification(user1, "Title 1", "Content 1"));
            notificationRepository.save(new Notification(user1, "Title 2", "Content 2"));
            notificationRepository.save(new Notification(user2, "Title 3", "Content 3"));

            // when
            int deletedCount = notificationRepository.deleteAllByReceiverId(user1.getId());

            // then
            assertThat(deletedCount).isEqualTo(2);
            assertThat(notificationRepository.findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(user1.getId()))
                .isEmpty();
            assertThat(notificationRepository.findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(user2.getId()))
                .hasSize(1);
        }

        @Test
        @DisplayName("확인된 알림도 함께 삭제")
        void deletesCheckedNotificationsAsWell() {
            // given
            notificationRepository.save(new Notification(user1, "Unchecked", "Content"));
            notificationRepository.save(new Notification(user1, "Checked", "Content").check());

            // when
            int deletedCount = notificationRepository.deleteAllByReceiverId(user1.getId());

            // then
            assertThat(deletedCount).isEqualTo(2);
        }

        @Test
        @DisplayName("알림이 없으면 0 반환")
        void returnsZero_whenNoNotifications() {
            // when
            int deletedCount = notificationRepository.deleteAllByReceiverId(user1.getId());

            // then
            assertThat(deletedCount).isZero();
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 삭제 시 0 반환")
        void returnsZero_whenUserDoesNotExist() {
            // given
            notificationRepository.save(new Notification(user1, "Title", "Content"));

            // when
            int deletedCount = notificationRepository.deleteAllByReceiverId(UUID.randomUUID());

            // then
            assertThat(deletedCount).isZero();
        }
    }
}
