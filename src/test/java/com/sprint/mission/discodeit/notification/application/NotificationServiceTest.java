package com.sprint.mission.discodeit.notification.application;

import com.sprint.mission.discodeit.notification.domain.Notification;
import com.sprint.mission.discodeit.notification.domain.NotificationRepository;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationCheckForbiddenException;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationNotFoundException;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService 단위 테스트")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    private static final UUID RECEIVER_ID = UUID.randomUUID();
    private static final UUID NOTIFICATION_ID = UUID.randomUUID();

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("유효한 요청 시 알림 생성 성공")
        void create_success() {
            // given
            String title = "새 메시지";
            String content = "테스트 메시지가 도착했습니다.";

            User receiver = mock(User.class);
            Notification savedNotification = mock(Notification.class);
            NotificationDto expectedDto = createNotificationDto(title, content);

            given(userRepository.findById(RECEIVER_ID)).willReturn(Optional.of(receiver));
            given(notificationRepository.save(any(Notification.class))).willReturn(savedNotification);
            given(notificationMapper.toDto(savedNotification)).willReturn(expectedDto);

            // when
            NotificationDto result = notificationService.create(RECEIVER_ID, title, content);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(notificationRepository).should().save(any(Notification.class));
        }

        @Test
        @DisplayName("수신자 없음 시 UserNotFoundException 발생")
        void create_withNonExistentReceiver_throwsException() {
            // given
            String title = "새 메시지";
            String content = "테스트 메시지입니다.";

            given(userRepository.findById(RECEIVER_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationService.create(RECEIVER_ID, title, content))
                .isInstanceOf(UserNotFoundException.class);

            then(notificationRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("findAllByReceiverId")
    class FindAllByReceiverIdTest {

        @Test
        @DisplayName("유효한 수신자 ID 조회 시 미확인 알림 목록 반환")
        void findAllByReceiverId_success() {
            // given
            Notification notification1 = mock(Notification.class);
            Notification notification2 = mock(Notification.class);
            NotificationDto dto1 = createNotificationDto("제목1", "내용1");
            NotificationDto dto2 = createNotificationDto("제목2", "내용2");

            given(notificationRepository.findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(RECEIVER_ID))
                .willReturn(List.of(notification1, notification2));
            given(notificationMapper.toDto(notification1)).willReturn(dto1);
            given(notificationMapper.toDto(notification2)).willReturn(dto2);

            // when
            List<NotificationDto> result = notificationService.findAllByReceiverId(RECEIVER_ID);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(dto1, dto2);
        }

        @Test
        @DisplayName("알림 없음 시 빈 리스트 반환")
        void findAllByReceiverId_whenEmpty_returnsEmptyList() {
            // given
            given(notificationRepository.findAllByReceiverIdAndCheckedFalseOrderByCreatedAtDesc(RECEIVER_ID))
                .willReturn(List.of());

            // when
            List<NotificationDto> result = notificationService.findAllByReceiverId(RECEIVER_ID);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("check")
    class CheckTest {

        @Test
        @DisplayName("유효한 요청 시 알림 확인 성공")
        void check_success() {
            // given
            User receiver = mock(User.class);
            Notification notification = mock(Notification.class);

            given(notificationRepository.findById(NOTIFICATION_ID)).willReturn(Optional.of(notification));
            given(notification.getReceiver()).willReturn(receiver);
            given(receiver.getId()).willReturn(RECEIVER_ID);
            given(notification.isChecked()).willReturn(false);

            // when
            notificationService.check(NOTIFICATION_ID, RECEIVER_ID);

            // then
            then(notification).should().check();
        }

        @Test
        @DisplayName("알림 없음 시 NotificationNotFoundException 발생")
        void check_withNonExistentNotification_throwsException() {
            // given
            given(notificationRepository.findById(NOTIFICATION_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationService.check(NOTIFICATION_ID, RECEIVER_ID))
                .isInstanceOf(NotificationNotFoundException.class);
        }

        @Test
        @DisplayName("다른 사용자 알림 확인 시 NotificationCheckForbiddenException 발생")
        void check_withDifferentUser_throwsException() {
            // given
            UUID otherUserId = UUID.randomUUID();
            User receiver = mock(User.class);
            Notification notification = mock(Notification.class);

            given(notificationRepository.findById(NOTIFICATION_ID)).willReturn(Optional.of(notification));
            given(notification.getReceiver()).willReturn(receiver);
            given(receiver.getId()).willReturn(RECEIVER_ID);

            // when & then
            assertThatThrownBy(() -> notificationService.check(NOTIFICATION_ID, otherUserId))
                .isInstanceOf(NotificationCheckForbiddenException.class);

            then(notification).should(never()).check();
        }

        @Test
        @DisplayName("수신자 null 알림 확인 시 NotificationCheckForbiddenException 발생")
        void check_withNullReceiver_throwsException() {
            // given
            Notification notification = mock(Notification.class);

            given(notificationRepository.findById(NOTIFICATION_ID)).willReturn(Optional.of(notification));
            given(notification.getReceiver()).willReturn(null);

            // when & then
            assertThatThrownBy(() -> notificationService.check(NOTIFICATION_ID, RECEIVER_ID))
                .isInstanceOf(NotificationCheckForbiddenException.class);

            then(notification).should(never()).check();
        }

        @Test
        @DisplayName("이미 확인한 알림 시 check() 호출하지 않음")
        void check_alreadyChecked_doesNothing() {
            // given
            User receiver = mock(User.class);
            Notification notification = mock(Notification.class);

            given(notificationRepository.findById(NOTIFICATION_ID)).willReturn(Optional.of(notification));
            given(notification.getReceiver()).willReturn(receiver);
            given(receiver.getId()).willReturn(RECEIVER_ID);
            given(notification.isChecked()).willReturn(true);

            // when
            notificationService.check(NOTIFICATION_ID, RECEIVER_ID);

            // then
            then(notification).should(never()).check();
        }
    }

    private NotificationDto createNotificationDto(String title, String content) {
        return new NotificationDto(
            NOTIFICATION_ID,
            Instant.now(),
            RECEIVER_ID,
            title,
            content
        );
    }
}
