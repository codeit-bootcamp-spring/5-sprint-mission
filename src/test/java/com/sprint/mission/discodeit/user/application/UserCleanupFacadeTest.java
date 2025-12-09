package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.notification.domain.NotificationRepository;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCleanupFacade 단위 테스트")
class UserCleanupFacadeTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UserCleanupFacade userCleanupFacade;

    private static final UUID USER_ID = UUID.randomUUID();

    @Nested
    @DisplayName("cleanup")
    class CleanupTest {

        @Test
        @DisplayName("사용자 삭제 시 관련 데이터 정리 성공")
        void cleanup_success() {
            // given
            UserDeletedEvent event = new UserDeletedEvent(USER_ID);

            given(messageRepository.nullifyAuthorByUserId(USER_ID)).willReturn(5);
            given(readStatusRepository.deleteAllByUserId(USER_ID)).willReturn(3);
            given(notificationRepository.deleteAllByReceiverId(USER_ID)).willReturn(2);

            // when
            userCleanupFacade.cleanup(event);

            // then
            then(messageRepository).should().nullifyAuthorByUserId(USER_ID);
            then(readStatusRepository).should().deleteAllByUserId(USER_ID);
            then(notificationRepository).should().deleteAllByReceiverId(USER_ID);
            then(cacheService).should().evict(CacheName.READ_STATUSES, USER_ID);
            then(cacheService).should().evict(CacheName.SUBSCRIBED_CHANNELS, USER_ID);
            then(cacheService).should().evict(CacheName.NOTIFICATIONS, USER_ID);
        }

        @Test
        @DisplayName("삭제할 데이터가 없어도 정상 처리")
        void cleanup_withNoData_success() {
            // given
            UserDeletedEvent event = new UserDeletedEvent(USER_ID);

            given(messageRepository.nullifyAuthorByUserId(USER_ID)).willReturn(0);
            given(readStatusRepository.deleteAllByUserId(USER_ID)).willReturn(0);
            given(notificationRepository.deleteAllByReceiverId(USER_ID)).willReturn(0);

            // when
            userCleanupFacade.cleanup(event);

            // then
            then(messageRepository).should().nullifyAuthorByUserId(USER_ID);
            then(readStatusRepository).should().deleteAllByUserId(USER_ID);
            then(notificationRepository).should().deleteAllByReceiverId(USER_ID);
            then(cacheService).should().evict(CacheName.READ_STATUSES, USER_ID);
            then(cacheService).should().evict(CacheName.SUBSCRIBED_CHANNELS, USER_ID);
            then(cacheService).should().evict(CacheName.NOTIFICATIONS, USER_ID);
        }

        @Test
        @DisplayName("데이터 정리 순서 검증")
        void cleanup_executesInOrder() {
            // given
            UserDeletedEvent event = new UserDeletedEvent(USER_ID);

            given(messageRepository.nullifyAuthorByUserId(USER_ID)).willReturn(1);
            given(readStatusRepository.deleteAllByUserId(USER_ID)).willReturn(1);
            given(notificationRepository.deleteAllByReceiverId(USER_ID)).willReturn(1);

            // when
            userCleanupFacade.cleanup(event);

            // then
            var inOrderVerifier = inOrder(
                messageRepository, readStatusRepository, notificationRepository, cacheService);

            inOrderVerifier.verify(messageRepository).nullifyAuthorByUserId(USER_ID);
            inOrderVerifier.verify(readStatusRepository).deleteAllByUserId(USER_ID);
            inOrderVerifier.verify(notificationRepository).deleteAllByReceiverId(USER_ID);
            inOrderVerifier.verify(cacheService).evict(CacheName.READ_STATUSES, USER_ID);
            inOrderVerifier.verify(cacheService).evict(CacheName.SUBSCRIBED_CHANNELS, USER_ID);
            inOrderVerifier.verify(cacheService).evict(CacheName.NOTIFICATIONS, USER_ID);
        }

        @Test
        @DisplayName("메시지 작성자 무효화 중 예외 발생 시 전파")
        void cleanup_whenMessageNullifyFails_throwsException() {
            // given
            UserDeletedEvent event = new UserDeletedEvent(USER_ID);
            RuntimeException exception = new RuntimeException("DB error");

            given(messageRepository.nullifyAuthorByUserId(USER_ID)).willThrow(exception);

            // when & then
            assertThatThrownBy(() -> userCleanupFacade.cleanup(event))
                .isInstanceOf(RuntimeException.class);

            then(readStatusRepository).should(never()).deleteAllByUserId(USER_ID);
            then(notificationRepository).should(never()).deleteAllByReceiverId(USER_ID);
            then(cacheService).should(never()).evict(CacheName.READ_STATUSES, USER_ID);
        }

        @Test
        @DisplayName("ReadStatus 삭제 중 예외 발생 시 전파")
        void cleanup_whenReadStatusDeleteFails_throwsException() {
            // given
            UserDeletedEvent event = new UserDeletedEvent(USER_ID);
            RuntimeException exception = new RuntimeException("ReadStatus delete error");

            given(messageRepository.nullifyAuthorByUserId(USER_ID)).willReturn(1);
            given(readStatusRepository.deleteAllByUserId(USER_ID)).willThrow(exception);

            // when & then
            assertThatThrownBy(() -> userCleanupFacade.cleanup(event))
                .isInstanceOf(RuntimeException.class);

            then(messageRepository).should().nullifyAuthorByUserId(USER_ID);
            then(notificationRepository).should(never()).deleteAllByReceiverId(USER_ID);
            then(cacheService).should(never()).evict(CacheName.READ_STATUSES, USER_ID);
        }

        @Test
        @DisplayName("Notification 삭제 중 예외 발생 시 전파")
        void cleanup_whenNotificationDeleteFails_throwsException() {
            // given
            UserDeletedEvent event = new UserDeletedEvent(USER_ID);
            RuntimeException exception = new RuntimeException("Notification delete error");

            given(messageRepository.nullifyAuthorByUserId(USER_ID)).willReturn(1);
            given(readStatusRepository.deleteAllByUserId(USER_ID)).willReturn(1);
            given(notificationRepository.deleteAllByReceiverId(USER_ID)).willThrow(exception);

            // when & then
            assertThatThrownBy(() -> userCleanupFacade.cleanup(event))
                .isInstanceOf(RuntimeException.class);

            then(messageRepository).should().nullifyAuthorByUserId(USER_ID);
            then(readStatusRepository).should().deleteAllByUserId(USER_ID);
            then(cacheService).should(never()).evict(CacheName.READ_STATUSES, USER_ID);
        }

        @Test
        @DisplayName("캐시 무효화 중 예외 발생 시 전파")
        void cleanup_whenCacheEvictFails_throwsException() {
            // given
            UserDeletedEvent event = new UserDeletedEvent(USER_ID);
            RuntimeException exception = new RuntimeException("Cache evict error");

            given(messageRepository.nullifyAuthorByUserId(USER_ID)).willReturn(1);
            given(readStatusRepository.deleteAllByUserId(USER_ID)).willReturn(1);
            given(notificationRepository.deleteAllByReceiverId(USER_ID)).willReturn(1);
            willThrow(exception).given(cacheService).evict(CacheName.READ_STATUSES, USER_ID);

            // when & then
            assertThatThrownBy(() -> userCleanupFacade.cleanup(event))
                .isInstanceOf(RuntimeException.class);
        }
    }
}
