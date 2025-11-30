package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationForbiddenException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
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

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("findAllByReceiverId - 수신자의 미확인 알림 목록 조회 성공")
    void findAllByReceiverId_Success() {
        // given
        UUID receiverId = UUID.randomUUID();
        User receiver = mock(User.class);

        Notification notification1 = mock(Notification.class);
        Notification notification2 = mock(Notification.class);
        List<Notification> notifications = List.of(notification1, notification2);

        NotificationDto dto1 = new NotificationDto(
            UUID.randomUUID(), Instant.now(), receiverId, "Title 1", "Content 1"
        );
        NotificationDto dto2 = new NotificationDto(
            UUID.randomUUID(), Instant.now(), receiverId, "Title 2", "Content 2"
        );

        given(userRepository.findById(receiverId)).willReturn(Optional.of(receiver));
        given(notificationRepository.findAllByReceiverAndCheckedFalseOrderByCreatedAtDesc(receiver))
            .willReturn(notifications);
        given(notificationMapper.toDto(notification1)).willReturn(dto1);
        given(notificationMapper.toDto(notification2)).willReturn(dto2);

        // when
        List<NotificationDto> result = notificationService.findAllByReceiverId(receiverId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);

        then(userRepository).should().findById(receiverId);
        then(notificationRepository).should()
            .findAllByReceiverAndCheckedFalseOrderByCreatedAtDesc(receiver);
    }

    @Test
    @DisplayName("findAllByReceiverId - 알림이 없는 경우 빈 목록 반환")
    void findAllByReceiverId_NoNotifications_ReturnsEmptyList() {
        // given
        UUID receiverId = UUID.randomUUID();
        User receiver = mock(User.class);

        given(userRepository.findById(receiverId)).willReturn(Optional.of(receiver));
        given(notificationRepository.findAllByReceiverAndCheckedFalseOrderByCreatedAtDesc(receiver))
            .willReturn(List.of());

        // when
        List<NotificationDto> result = notificationService.findAllByReceiverId(receiverId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllByReceiverId - 존재하지 않는 사용자 조회 시 UserNotFoundException 발생")
    void findAllByReceiverId_UserNotFound_ThrowsUserNotFoundException() {
        // given
        UUID receiverId = UUID.randomUUID();

        given(userRepository.findById(receiverId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.findAllByReceiverId(receiverId))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(receiverId);
        then(notificationRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("check - 알림 확인 성공")
    void check_Success() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        User receiver = mock(User.class);
        Notification notification = mock(Notification.class);

        given(receiver.getId()).willReturn(requesterId);
        given(notification.getReceiver()).willReturn(receiver);
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

        // when
        notificationService.check(notificationId, requesterId);

        // then
        then(notificationRepository).should().findById(notificationId);
        then(notification).should().check();
    }

    @Test
    @DisplayName("check - 존재하지 않는 알림 확인 시 NotificationNotFoundException 발생")
    void check_NotificationNotFound_ThrowsNotificationNotFoundException() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        given(notificationRepository.findById(notificationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.check(notificationId, requesterId))
            .isInstanceOf(NotificationNotFoundException.class);

        then(notificationRepository).should().findById(notificationId);
    }

    @Test
    @DisplayName("check - 다른 사용자의 알림 확인 시 NotificationForbiddenException 발생")
    void check_Forbidden_ThrowsNotificationForbiddenException() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        User owner = mock(User.class);
        Notification notification = mock(Notification.class);

        given(owner.getId()).willReturn(ownerId);
        given(notification.getReceiver()).willReturn(owner);
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

        // when & then
        assertThatThrownBy(() -> notificationService.check(notificationId, requesterId))
            .isInstanceOf(NotificationForbiddenException.class);

        then(notificationRepository).should().findById(notificationId);
        then(notification).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("create - 알림 생성 성공")
    void create_Success() {
        // given
        UUID receiverId = UUID.randomUUID();
        String title = "New Message";
        String content = "Hello, World!";

        User receiver = new User("testuser", "test@example.com", "encoded", null);
        Notification savedNotification = new Notification(receiver, title, content);

        NotificationDto expectedDto = new NotificationDto(
            UUID.randomUUID(),
            Instant.now(),
            receiverId,
            title,
            content
        );

        given(userRepository.findById(receiverId)).willReturn(Optional.of(receiver));
        given(notificationRepository.save(any(Notification.class))).willReturn(savedNotification);
        given(notificationMapper.toDto(savedNotification)).willReturn(expectedDto);

        // when
        NotificationDto result = notificationService.create(receiverId, title, content);

        // then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo(title);
        assertThat(result.content()).isEqualTo(content);

        then(userRepository).should().findById(receiverId);
        then(notificationRepository).should().save(any(Notification.class));
        then(notificationMapper).should().toDto(savedNotification);
    }

    @Test
    @DisplayName("create - 존재하지 않는 사용자에게 알림 생성 시 UserNotFoundException 발생")
    void create_UserNotFound_ThrowsUserNotFoundException() {
        // given
        UUID receiverId = UUID.randomUUID();
        String title = "New Message";
        String content = "Hello, World!";

        given(userRepository.findById(receiverId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.create(receiverId, title, content))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(receiverId);
        then(notificationRepository).shouldHaveNoInteractions();
        then(notificationMapper).shouldHaveNoInteractions();
    }
}
