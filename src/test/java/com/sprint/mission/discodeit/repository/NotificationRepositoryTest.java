package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.notification.domain.Notification;
import com.sprint.mission.discodeit.domain.notification.domain.NotificationRepository;
import com.sprint.mission.discodeit.domain.user.domain.User;
import com.sprint.mission.discodeit.domain.user.domain.UserRepository;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@DisplayName("NotificationRepository 슬라이스 테스트")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User receiver;
    private Notification uncheckedNotification1;
    private Notification uncheckedNotification2;

    @BeforeEach
    void setUp() {
        receiver = userRepository.save(new User("testuser", "test@example.com", "password1234", null));

        uncheckedNotification1 = notificationRepository.save(new Notification(receiver, "Title 1", "Content 1"));
        entityManager.flush();

        uncheckedNotification2 = notificationRepository.save(new Notification(receiver, "Title 2", "Content 2"));
        entityManager.flush();

        Notification checkedNotification = new Notification(receiver, "Title 3", "Content 3");
        checkedNotification.check();
        notificationRepository.save(checkedNotification);
        entityManager.flush();
    }

    @Nested
    @DisplayName("findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc")
    class FindByReceiverIdAndCheckedFalseOrderByCreatedAtDesc {

        @Test
        @DisplayName("읽지 않은 알림을 최신순으로 조회한다")
        void findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc_returnsUncheckedNotifications() {
            // when
            List<Notification> notifications = notificationRepository
                .findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(receiver.getId());

            // then
            assertThat(notifications).hasSize(2);
            assertThat(notifications).allMatch(n -> !n.isChecked());
        }

        @Test
        @DisplayName("최신 알림이 먼저 조회된다")
        void findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc_orderedByCreatedAtDesc() {
            // when
            List<Notification> notifications = notificationRepository
                .findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(receiver.getId());

            // then
            assertThat(notifications).hasSize(2);
            assertThat(notifications.get(0).getCreatedAt())
                .isAfterOrEqualTo(notifications.get(1).getCreatedAt());
        }

        @Test
        @DisplayName("모든 알림이 읽음 상태면 빈 목록을 반환한다")
        void findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc_allChecked_returnsEmptyList() {
            // given
            uncheckedNotification1.check();
            uncheckedNotification2.check();
            notificationRepository.saveAll(List.of(uncheckedNotification1, uncheckedNotification2));

            // when
            List<Notification> notifications = notificationRepository
                .findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(receiver.getId());

            // then
            assertThat(notifications).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteByReceiverId")
    class DeleteByReceiverId {

        @Test
        @DisplayName("수신자 ID로 모든 알림을 삭제한다")
        void deleteByReceiverId_deletesAllNotifications() {
            // when
            notificationRepository.deleteByReceiverId(receiver.getId());

            // then
            List<Notification> remaining = notificationRepository
                .findByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(receiver.getId());
            assertThat(remaining).isEmpty();
            assertThat(notificationRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("다른 수신자의 알림은 삭제되지 않는다")
        void deleteByReceiverId_doesNotDeleteOtherReceiverNotifications() {
            // given
            User otherReceiver = userRepository.save(
                new User("otheruser", "other@example.com", "password1234", null));
            notificationRepository.save(new Notification(otherReceiver, "Other Title", "Other Content"));

            // when
            notificationRepository.deleteByReceiverId(receiver.getId());

            // then
            assertThat(notificationRepository.findAll()).hasSize(1);
        }
    }
}
