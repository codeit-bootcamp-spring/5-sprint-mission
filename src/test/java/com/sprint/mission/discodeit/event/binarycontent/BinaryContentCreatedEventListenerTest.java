package com.sprint.mission.discodeit.event.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createUserWithId;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BinaryContentCreatedEventListenerTest {

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentService binaryContentService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BinaryContentCreatedEventListener listener;

    @Test
    @DisplayName("바이너리 콘텐츠 저장 성공 시 상태를 SUCCESS로 업데이트")
    void handleBinaryContentCreatedEvent_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, bytes);

        // when
        listener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, bytes);
        then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
    }

    @Test
    @DisplayName("storeWithRetry 메서드가 정상적으로 스토리지에 저장")
    void storeWithRetry_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();

        // when
        listener.storeWithRetry(binaryContentId, bytes);

        // then
        then(binaryContentStorage).should().put(binaryContentId, bytes);
    }

    @Test
    @DisplayName("recover 메서드 호출 시 상태를 FAIL로 업데이트하고 관리자에게 알림 전송 후 예외 발생")
    void recover_UpdatesStatusAndNotifiesAdmins() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        Exception exception = new RuntimeException("Storage error");
        String requestId = "test-request-id";

        UUID adminId = UUID.randomUUID();
        User admin = createUserWithId(adminId, "admin");

        given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of(admin));

        MDC.put("requestId", requestId);

        try {
            // when & then
            assertThatThrownBy(() -> listener.recover(exception, binaryContentId, bytes))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("바이너리 콘텐츠 저장 실패");

            then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.FAIL);
            then(userRepository).should().findAllByRole(Role.ADMIN);
            then(notificationService).should().create(
                eq(adminId),
                eq("바이너리 콘텐츠 저장 실패"),
                any(String.class)
            );
        } finally {
            MDC.clear();
        }
    }

    @Test
    @DisplayName("recover 시 관리자가 없으면 알림을 보내지 않고 예외 발생")
    void recover_NoAdmins_DoesNotNotify() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        Exception exception = new RuntimeException("Storage error");

        given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> listener.recover(exception, binaryContentId, bytes))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("바이너리 콘텐츠 저장 실패");

        then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.FAIL);
        then(userRepository).should().findAllByRole(Role.ADMIN);
        then(notificationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("recover 시 여러 관리자에게 알림 전송 후 예외 발생")
    void recover_MultipleAdmins_NotifiesAll() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        Exception exception = new RuntimeException("Storage error");

        UUID admin1Id = UUID.randomUUID();
        UUID admin2Id = UUID.randomUUID();

        User admin1 = createUserWithId(admin1Id, "admin1");
        User admin2 = createUserWithId(admin2Id, "admin2");

        given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of(admin1, admin2));

        // when & then
        assertThatThrownBy(() -> listener.recover(exception, binaryContentId, bytes))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("바이너리 콘텐츠 저장 실패");

        then(notificationService).should().create(eq(admin1Id), any(), any());
        then(notificationService).should().create(eq(admin2Id), any(), any());
    }
}
