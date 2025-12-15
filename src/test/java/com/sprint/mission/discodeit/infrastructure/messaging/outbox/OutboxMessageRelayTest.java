package com.sprint.mission.discodeit.infrastructure.messaging.outbox;

import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxMessageRelay 단위 테스트")
class OutboxMessageRelayTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OutboxMessageRelay outboxMessageRelay;

    private static final int BATCH_SIZE = 50;

    @Nested
    @DisplayName("publishEvents")
    class PublishEvents {

        @Test
        @DisplayName("대기 중인 이벤트가 없으면 아무것도 발행하지 않음")
        void publishEvents_noEvents_doesNothing() {
            // given
            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            given(outboxEventRepository.findAllByOrderByCreatedAtAsc(pageable))
                .willReturn(Collections.emptyList());

            // when
            outboxMessageRelay.publishEvents();

            // then
            then(kafkaTemplate).should(never()).send(any(), any(), any());
            then(outboxEventRepository).should(never()).delete(any(OutboxEvent.class));
        }

        @Test
        @DisplayName("단일 이벤트 발행 성공 시 Kafka로 전송하고 삭제")
        void publishEvents_singleEvent_sendsToKafkaAndDeletes() {
            // given
            UUID aggregateId = UUID.randomUUID();
            UUID eventId = UUID.randomUUID();
            String topic = "test.topic";
            String payload = "{\"data\":\"test\"}";

            OutboxEvent event = createOutboxEvent(eventId, AggregateType.USER, aggregateId, topic, payload);

            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            given(outboxEventRepository.findAllByOrderByCreatedAtAsc(pageable))
                .willReturn(List.of(event));

            CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
            given(kafkaTemplate.send(topic, aggregateId.toString(), payload)).willReturn(future);

            // when
            outboxMessageRelay.publishEvents();

            // then
            then(kafkaTemplate).should().send(topic, aggregateId.toString(), payload);
            then(outboxEventRepository).should().delete(event);
        }

        @Test
        @DisplayName("다수의 이벤트 발행 성공 시 모두 전송하고 개별 삭제")
        void publishEvents_multipleEvents_sendsAllToKafkaAndDeletesEach() {
            // given
            OutboxEvent event1 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.USER, UUID.randomUUID(),
                "topic1", "{\"data\":\"event1\"}"
            );
            OutboxEvent event2 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.CHANNEL, UUID.randomUUID(),
                "topic2", "{\"data\":\"event2\"}"
            );
            OutboxEvent event3 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.MESSAGE, UUID.randomUUID(),
                "topic3", "{\"data\":\"event3\"}"
            );

            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            given(outboxEventRepository.findAllByOrderByCreatedAtAsc(pageable))
                .willReturn(List.of(event1, event2, event3));

            CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
            given(kafkaTemplate.send(any(), any(), any())).willReturn(future);

            // when
            outboxMessageRelay.publishEvents();

            // then
            then(kafkaTemplate).should(times(3)).send(any(), any(), any());
            then(outboxEventRepository).should().delete(event1);
            then(outboxEventRepository).should().delete(event2);
            then(outboxEventRepository).should().delete(event3);
        }

        @Test
        @DisplayName("이벤트 발행 시 올바른 topic과 key 사용")
        void publishEvents_usesCorrectTopicAndKey() {
            // given
            UUID aggregateId = UUID.randomUUID();
            String topic = "discodeit.message.created";
            String payload = "{\"messageId\":\"123\"}";

            OutboxEvent event = createOutboxEvent(
                UUID.randomUUID(), AggregateType.MESSAGE, aggregateId, topic, payload
            );

            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            given(outboxEventRepository.findAllByOrderByCreatedAtAsc(pageable))
                .willReturn(List.of(event));

            CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
            given(kafkaTemplate.send(eq(topic), eq(aggregateId.toString()), eq(payload)))
                .willReturn(future);

            // when
            outboxMessageRelay.publishEvents();

            // then
            ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);

            then(kafkaTemplate).should().send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                payloadCaptor.capture()
            );

            assertThat(topicCaptor.getValue()).isEqualTo(topic);
            assertThat(keyCaptor.getValue()).isEqualTo(aggregateId.toString());
            assertThat(payloadCaptor.getValue()).isEqualTo(payload);
        }

        @Test
        @DisplayName("배치 사이즈만큼 이벤트 조회")
        void publishEvents_queriesWithCorrectBatchSize() {
            // given
            given(outboxEventRepository.findAllByOrderByCreatedAtAsc(any(Pageable.class)))
                .willReturn(Collections.emptyList());

            // when
            outboxMessageRelay.publishEvents();

            // then
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            then(outboxEventRepository).should().findAllByOrderByCreatedAtAsc(pageableCaptor.capture());

            Pageable capturedPageable = pageableCaptor.getValue();
            assertThat(capturedPageable.getPageNumber()).isZero();
            assertThat(capturedPageable.getPageSize()).isEqualTo(BATCH_SIZE);
        }

        @Test
        @DisplayName("Kafka 전송 실패 시 해당 이벤트는 삭제하지 않고 나머지 계속 처리")
        void publishEvents_kafkaFailure_continuesProcessingAndDeletesOnlySuccessful() {
            // given
            OutboxEvent event1 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.USER, UUID.randomUUID(),
                "topic1", "{\"data\":\"event1\"}"
            );
            OutboxEvent event2 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.CHANNEL, UUID.randomUUID(),
                "topic2", "{\"data\":\"event2\"}"
            );
            OutboxEvent event3 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.MESSAGE, UUID.randomUUID(),
                "topic3", "{\"data\":\"event3\"}"
            );

            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            given(outboxEventRepository.findAllByOrderByCreatedAtAsc(pageable))
                .willReturn(List.of(event1, event2, event3));

            // event1: 실패, event2: 성공, event3: 성공
            CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Kafka error"));

            CompletableFuture<SendResult<String, String>> successFuture = CompletableFuture.completedFuture(null);

            given(kafkaTemplate.send(eq("topic1"), any(), any())).willReturn(failedFuture);
            given(kafkaTemplate.send(eq("topic2"), any(), any())).willReturn(successFuture);
            given(kafkaTemplate.send(eq("topic3"), any(), any())).willReturn(successFuture);

            // when
            outboxMessageRelay.publishEvents();

            // then
            then(kafkaTemplate).should(times(3)).send(any(), any(), any());
            // event1은 삭제되지 않고, event2, event3만 삭제
            then(outboxEventRepository).should(never()).delete(event1);
            then(outboxEventRepository).should().delete(event2);
            then(outboxEventRepository).should().delete(event3);
        }

        @Test
        @DisplayName("Kafka 전송 타임아웃 시 해당 이벤트는 삭제하지 않음")
        void publishEvents_kafkaTimeout_doesNotDeleteTimedOutEvent() {
            // given
            OutboxEvent event1 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.USER, UUID.randomUUID(),
                "topic1", "{\"data\":\"event1\"}"
            );
            OutboxEvent event2 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.CHANNEL, UUID.randomUUID(),
                "topic2", "{\"data\":\"event2\"}"
            );

            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            given(outboxEventRepository.findAllByOrderByCreatedAtAsc(pageable))
                .willReturn(List.of(event1, event2));

            // event1: 타임아웃, event2: 성공
            CompletableFuture<SendResult<String, String>> timeoutFuture = new CompletableFuture<>();
            timeoutFuture.completeExceptionally(new TimeoutException("Timeout"));

            CompletableFuture<SendResult<String, String>> successFuture = CompletableFuture.completedFuture(null);

            given(kafkaTemplate.send(eq("topic1"), any(), any())).willReturn(timeoutFuture);
            given(kafkaTemplate.send(eq("topic2"), any(), any())).willReturn(successFuture);

            // when
            outboxMessageRelay.publishEvents();

            // then
            then(outboxEventRepository).should(never()).delete(event1);
            then(outboxEventRepository).should().delete(event2);
        }

        @Test
        @DisplayName("모든 이벤트 발행 실패 시 아무것도 삭제하지 않음")
        void publishEvents_allFailed_deletesNothing() {
            // given
            OutboxEvent event1 = createOutboxEvent(
                UUID.randomUUID(), AggregateType.USER, UUID.randomUUID(),
                "topic1", "{\"data\":\"event1\"}"
            );

            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            given(outboxEventRepository.findAllByOrderByCreatedAtAsc(pageable))
                .willReturn(List.of(event1));

            CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Kafka error"));
            given(kafkaTemplate.send(any(), any(), any())).willReturn(failedFuture);

            // when
            outboxMessageRelay.publishEvents();

            // then
            then(outboxEventRepository).should(never()).delete(any(OutboxEvent.class));
        }
    }

    private OutboxEvent createOutboxEvent(
        UUID id, AggregateType aggregateType, UUID aggregateId, String topic, String payload
    ) {
        OutboxEvent event = new OutboxEvent(aggregateType, aggregateId, topic, payload);
        ReflectionTestUtils.setField(event, "id", id);
        return event;
    }
}
