package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStorage;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentStorageException;
import com.sprint.mission.discodeit.notification.application.NotificationService;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("BinaryContentStorageProcessor 단위 테스트")
class BinaryContentStorageProcessorTest {

    @Mock
    private BinaryContentService binaryContentService;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BinaryContentStorageProcessor storageProcessor;

    private static final UUID TEST_BINARY_CONTENT_ID = UUID.randomUUID();
    private static final byte[] TEST_BYTES = "test content".getBytes();

    @Nested
    @DisplayName("processWithRetry 메서드")
    class ProcessWithRetry {

        @Test
        @DisplayName("파일 저장 성공 시 상태를 SUCCESS로 변경")
        void processWithRetry_onSuccess_updatesStatusToSuccess() {
            // given
            BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(TEST_BINARY_CONTENT_ID, TEST_BYTES);

            // when
            storageProcessor.processWithRetry(event);

            // then
            then(binaryContentStorage).should().put(TEST_BINARY_CONTENT_ID, TEST_BYTES);
            then(binaryContentService).should().updateStatus(TEST_BINARY_CONTENT_ID, BinaryContentStatus.SUCCESS);
        }

        @Test
        @DisplayName("Storage 예외 발생 시 예외 전파")
        void processWithRetry_onStorageException_throwsException() {
            // given
            BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(TEST_BINARY_CONTENT_ID, TEST_BYTES);

            willThrow(new BinaryContentStorageException(new RuntimeException("Storage error")))
                .given(binaryContentStorage).put(TEST_BINARY_CONTENT_ID, TEST_BYTES);

            // when & then
            assertThatThrownBy(() -> storageProcessor.processWithRetry(event))
                .isInstanceOf(BinaryContentStorageException.class);

            then(binaryContentService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("RuntimeException 발생 시 예외 전파")
        void processWithRetry_onRuntimeException_throwsException() {
            // given
            BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(TEST_BINARY_CONTENT_ID, TEST_BYTES);

            willThrow(new RuntimeException("Unexpected error"))
                .given(binaryContentStorage).put(TEST_BINARY_CONTENT_ID, TEST_BYTES);

            // when & then
            assertThatThrownBy(() -> storageProcessor.processWithRetry(event))
                .isInstanceOf(RuntimeException.class);

            then(binaryContentService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("recover 메서드")
    class Recover {

        private static final UUID ADMIN_ID = UUID.randomUUID();

        @Test
        @DisplayName("재시도 실패 시 상태를 FAIL로 변경하고 관리자에게 알림 발송")
        void recover_afterRetryExhausted_updatesStatusToFailAndNotifiesAdmins() {
            // given
            BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(TEST_BINARY_CONTENT_ID, TEST_BYTES);
            Exception exception = new RuntimeException("Storage failed");

            User admin = new User("admin", "admin@test.com", "password", null);
            ReflectionTestUtils.setField(admin, "id", ADMIN_ID);

            given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of(admin));

            // when
            storageProcessor.recover(exception, event);

            // then
            then(binaryContentService).should().updateStatus(TEST_BINARY_CONTENT_ID, BinaryContentStatus.FAIL);
            then(notificationService).should().create(eq(ADMIN_ID), anyString(), anyString());
        }

        @Test
        @DisplayName("BinaryContentStorageException으로 recover 호출 시 상태를 FAIL로 변경하고 관리자에게 알림 발송")
        void recover_withStorageException_updatesStatusToFailAndNotifiesAdmins() {
            // given
            BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(TEST_BINARY_CONTENT_ID, TEST_BYTES);
            Exception exception = new BinaryContentStorageException(new RuntimeException("S3 upload failed"));

            User admin = new User("admin", "admin@test.com", "password", null);
            ReflectionTestUtils.setField(admin, "id", ADMIN_ID);

            given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of(admin));

            // when
            storageProcessor.recover(exception, event);

            // then
            then(binaryContentService).should().updateStatus(TEST_BINARY_CONTENT_ID, BinaryContentStatus.FAIL);
            then(notificationService).should().create(eq(ADMIN_ID), anyString(), anyString());
        }

        @Test
        @DisplayName("관리자가 없을 경우 알림 발송 없이 상태만 FAIL로 변경")
        void recover_withNoAdmins_updatesStatusToFailWithoutNotification() {
            // given
            BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(TEST_BINARY_CONTENT_ID, TEST_BYTES);
            Exception exception = new RuntimeException("Storage failed");

            given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of());

            // when
            storageProcessor.recover(exception, event);

            // then
            then(binaryContentService).should().updateStatus(TEST_BINARY_CONTENT_ID, BinaryContentStatus.FAIL);
            then(notificationService).shouldHaveNoInteractions();
        }
    }
}
