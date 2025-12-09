package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import com.sprint.mission.discodeit.common.infrastructure.outbox.OutboxEventWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelOutboxHandleListener 단위 테스트")
class ChannelOutboxHandleListenerTest {

    @Mock
    private OutboxEventWriter outboxEventWriter;

    @InjectMocks
    private ChannelOutboxHandleListener listener;

    @Test
    @DisplayName("ChannelDeletedEvent 수신 시 OutboxEventWriter에 이벤트 저장")
    void on_withChannelDeletedEvent_writesToOutbox() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelDeletedEvent event = new ChannelDeletedEvent(channelId, ChannelType.PUBLIC);

        // when
        listener.on(event);

        // then
        then(outboxEventWriter).should().write(
            AggregateType.CHANNEL,
            channelId,
            ChannelDeletedEvent.TOPIC,
            event
        );
    }

    @Test
    @DisplayName("PRIVATE 채널 삭제 이벤트도 동일하게 처리")
    void on_withPrivateChannelDeletedEvent_writesToOutbox() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelDeletedEvent event = new ChannelDeletedEvent(channelId, ChannelType.PRIVATE);

        // when
        listener.on(event);

        // then
        then(outboxEventWriter).should().write(
            AggregateType.CHANNEL,
            channelId,
            ChannelDeletedEvent.TOPIC,
            event
        );
    }
}
