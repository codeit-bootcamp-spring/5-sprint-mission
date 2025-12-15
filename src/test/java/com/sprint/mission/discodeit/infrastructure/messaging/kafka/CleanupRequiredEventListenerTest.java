package com.sprint.mission.discodeit.infrastructure.messaging.kafka;

import com.sprint.mission.discodeit.channel.application.ChannelCleanupFacade;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.message.application.MessageCleanupFacade;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.user.application.UserCleanupFacade;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("CleanupRequiredEventListener 단위 테스트")
class CleanupRequiredEventListenerTest {

    @Mock
    private ChannelCleanupFacade channelCleanupFacade;

    @Mock
    private MessageCleanupFacade messageCleanupFacade;

    @Mock
    private UserCleanupFacade userCleanupFacade;

    @InjectMocks
    private CleanupRequiredEventListener listener;

    @Nested
    @DisplayName("onChannelDeletedEvent")
    class OnChannelDeletedEvent {

        @Test
        @DisplayName("PUBLIC 채널 삭제 이벤트 수신 시 ChannelCleanupFacade 호출")
        void onChannelDeletedEvent_publicChannel_callsChannelCleanupFacade() {
            // given
            UUID channelId = UUID.randomUUID();
            ChannelDeletedEvent event = new ChannelDeletedEvent(channelId, ChannelType.PUBLIC);

            // when
            listener.onChannelDeletedEvent(event);

            // then
            then(channelCleanupFacade).should().cleanup(event);
        }

        @Test
        @DisplayName("PRIVATE 채널 삭제 이벤트 수신 시 ChannelCleanupFacade 호출")
        void onChannelDeletedEvent_privateChannel_callsChannelCleanupFacade() {
            // given
            UUID channelId = UUID.randomUUID();
            ChannelDeletedEvent event = new ChannelDeletedEvent(channelId, ChannelType.PRIVATE);

            // when
            listener.onChannelDeletedEvent(event);

            // then
            then(channelCleanupFacade).should().cleanup(event);
        }
    }

    @Nested
    @DisplayName("onMessageDeletedEvent")
    class OnMessageDeletedEvent {

        @Test
        @DisplayName("메시지 삭제 이벤트 수신 시 MessageCleanupFacade 호출")
        void onMessageDeletedEvent_callsMessageCleanupFacade() {
            // given
            UUID messageId = UUID.randomUUID();
            MessageDeletedEvent event = new MessageDeletedEvent(messageId);

            // when
            listener.onMessageDeletedEvent(event);

            // then
            then(messageCleanupFacade).should().cleanup(event);
        }
    }

    @Nested
    @DisplayName("onUserDeletedEvent")
    class OnUserDeletedEvent {

        @Test
        @DisplayName("사용자 삭제 이벤트 수신 시 UserCleanupFacade 호출")
        void onUserDeletedEvent_callsUserCleanupFacade() {
            // given
            UUID userId = UUID.randomUUID();
            UserDeletedEvent event = new UserDeletedEvent(userId);

            // when
            listener.onUserDeletedEvent(event);

            // then
            then(userCleanupFacade).should().cleanup(event);
        }
    }

    @Nested
    @DisplayName("handleDlt")
    class HandleDlt {

        @Test
        @DisplayName("DLT 메시지 수신 시 예외 없이 처리")
        void handleDlt_logsErrorWithoutException() {
            // given
            byte[] payload = "{\"channelId\":\"test-id\"}".getBytes();
            String topic = "discodeit.channel.deleted";
            String key = UUID.randomUUID().toString();
            String exceptionMessage = "Deserialization error";

            // when & then (no exception thrown)
            listener.handleDlt(payload, topic, key, exceptionMessage);
        }

        @Test
        @DisplayName("채널 삭제 토픽 DLT 메시지 처리")
        void handleDlt_channelDeletedTopic_handlesWithoutException() {
            // given
            byte[] payload = "invalid-payload".getBytes();
            String topic = ChannelDeletedEvent.TOPIC;
            String key = UUID.randomUUID().toString();
            String exceptionMessage = "Failed to deserialize ChannelDeletedEvent";

            // when & then (no exception thrown)
            listener.handleDlt(payload, topic, key, exceptionMessage);
        }

        @Test
        @DisplayName("메시지 삭제 토픽 DLT 메시지 처리")
        void handleDlt_messageDeletedTopic_handlesWithoutException() {
            // given
            byte[] payload = "corrupted-data".getBytes();
            String topic = MessageDeletedEvent.TOPIC;
            String key = UUID.randomUUID().toString();
            String exceptionMessage = "Failed to process MessageDeletedEvent";

            // when & then (no exception thrown)
            listener.handleDlt(payload, topic, key, exceptionMessage);
        }

        @Test
        @DisplayName("사용자 삭제 토픽 DLT 메시지 처리")
        void handleDlt_userDeletedTopic_handlesWithoutException() {
            // given
            byte[] payload = new byte[0];
            String topic = UserDeletedEvent.TOPIC;
            String key = UUID.randomUUID().toString();
            String exceptionMessage = "Empty payload received";

            // when & then (no exception thrown)
            listener.handleDlt(payload, topic, key, exceptionMessage);
        }

        @Test
        @DisplayName("빈 페이로드로 DLT 메시지 처리")
        void handleDlt_emptyPayload_handlesWithoutException() {
            // given
            byte[] payload = new byte[0];
            String topic = "unknown.topic";
            String key = "unknown-key";
            String exceptionMessage = "Unknown error occurred";

            // when & then (no exception thrown)
            listener.handleDlt(payload, topic, key, exceptionMessage);
        }
    }
}
