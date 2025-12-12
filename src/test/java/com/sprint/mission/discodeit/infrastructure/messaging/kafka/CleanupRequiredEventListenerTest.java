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
}
