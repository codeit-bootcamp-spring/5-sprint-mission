package com.sprint.mission.discodeit.infrastructure.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SameParameterValue")
@DisplayName("OutboxEventWriterImpl 단위 테스트")
class OutboxEventWriterImplTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxEventWriterImpl outboxEventWriter;

    @Nested
    @DisplayName("write")
    class Write {

        @Test
        @DisplayName("이벤트 직렬화 및 OutboxEvent 저장 성공")
        void write_serializesEventAndSavesOutboxEvent() throws JsonProcessingException {
            // given
            UUID aggregateId = UUID.randomUUID();
            String topic = "test.topic";
            TestEvent event = new TestEvent("testData", 123);
            String serializedPayload = "{\"data\":\"testData\",\"value\":123}";

            given(objectMapper.writeValueAsString(event)).willReturn(serializedPayload);

            ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);

            // when
            outboxEventWriter.write(AggregateType.USER, aggregateId, topic, event);

            // then
            then(outboxEventRepository).should().save(captor.capture());

            OutboxEvent savedEvent = captor.getValue();
            assertThat(savedEvent.getAggregateType()).isEqualTo(AggregateType.USER);
            assertThat(savedEvent.getAggregateId()).isEqualTo(aggregateId);
            assertThat(savedEvent.getTopic()).isEqualTo(topic);
            assertThat(savedEvent.getPayload()).isEqualTo(serializedPayload);
        }

        @Test
        @DisplayName("CHANNEL AggregateType으로 OutboxEvent 저장")
        void write_withChannelAggregateType_savesOutboxEvent() throws JsonProcessingException {
            // given
            UUID aggregateId = UUID.randomUUID();
            String topic = "discodeit.channel.deleted";
            TestEvent event = new TestEvent("channelData", 456);
            String serializedPayload = "{\"data\":\"channelData\",\"value\":456}";

            given(objectMapper.writeValueAsString(event)).willReturn(serializedPayload);

            ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);

            // when
            outboxEventWriter.write(AggregateType.CHANNEL, aggregateId, topic, event);

            // then
            then(outboxEventRepository).should().save(captor.capture());

            OutboxEvent savedEvent = captor.getValue();
            assertThat(savedEvent.getAggregateType()).isEqualTo(AggregateType.CHANNEL);
            assertThat(savedEvent.getAggregateId()).isEqualTo(aggregateId);
            assertThat(savedEvent.getTopic()).isEqualTo(topic);
        }

        @Test
        @DisplayName("MESSAGE AggregateType으로 OutboxEvent 저장")
        void write_withMessageAggregateType_savesOutboxEvent() throws JsonProcessingException {
            // given
            UUID aggregateId = UUID.randomUUID();
            String topic = "discodeit.message.created";
            TestEvent event = new TestEvent("messageData", 789);
            String serializedPayload = "{\"data\":\"messageData\",\"value\":789}";

            given(objectMapper.writeValueAsString(event)).willReturn(serializedPayload);

            ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);

            // when
            outboxEventWriter.write(AggregateType.MESSAGE, aggregateId, topic, event);

            // then
            then(outboxEventRepository).should().save(captor.capture());

            OutboxEvent savedEvent = captor.getValue();
            assertThat(savedEvent.getAggregateType()).isEqualTo(AggregateType.MESSAGE);
        }

        @Test
        @DisplayName("직렬화 실패 시 저장하지 않음")
        void write_serializationFails_doesNotSave() throws JsonProcessingException {
            // given
            UUID aggregateId = UUID.randomUUID();
            String topic = "test.topic";
            TestEvent event = new TestEvent("testData", 123);

            given(objectMapper.writeValueAsString(event))
                .willThrow(new JsonProcessingException("Serialization failed") {
                });

            // when
            outboxEventWriter.write(AggregateType.USER, aggregateId, topic, event);

            // then
            then(outboxEventRepository).should(never()).save(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("복잡한 이벤트 객체 직렬화 및 저장")
        void write_complexEvent_serializesAndSaves() throws JsonProcessingException {
            // given
            UUID aggregateId = UUID.randomUUID();
            String topic = "complex.event.topic";
            ComplexEvent event = new ComplexEvent(
                UUID.randomUUID(),
                "complexData",
                new NestedData("nested", 100)
            );
            String serializedPayload = "{\"id\":\"...\",\"data\":\"complexData\",\"nested\":{\"name\":\"nested\",\"value\":100}}";

            given(objectMapper.writeValueAsString(event)).willReturn(serializedPayload);

            ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);

            // when
            outboxEventWriter.write(AggregateType.NOTIFICATION, aggregateId, topic, event);

            // then
            then(outboxEventRepository).should().save(captor.capture());

            OutboxEvent savedEvent = captor.getValue();
            assertThat(savedEvent.getPayload()).isEqualTo(serializedPayload);
        }
    }

    private record TestEvent(String data, int value) {
    }

    private record ComplexEvent(UUID id, String data, NestedData nested) {
    }

    private record NestedData(String name, int value) {
    }
}
