package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class KafkaEventDispatcherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaEventDispatcher listener;

    @Test
    @DisplayName("MessageCreatedEvent 발생 시 Kafka로 메시지 전송")
    void on_MessageCreatedEvent_SendsToKafka() throws JsonProcessingException {
        // given
        UUID messageId = UUID.randomUUID();
        MessageCreatedEvent event = new MessageCreatedEvent(messageId);
        String expectedPayload = "{\"messageId\":\"" + messageId + "\"}";

        given(objectMapper.writeValueAsString(event)).willReturn(expectedPayload);

        // when
        listener.on(event);

        // then
        then(kafkaTemplate).should().send(
            Topic.MESSAGE_CREATED.getValue(),
            messageId.toString(),
            expectedPayload
        );
    }

    @Test
    @DisplayName("RoleUpdatedEvent 발생 시 Kafka로 메시지 전송")
    void on_RoleUpdatedEvent_SendsToKafka() throws JsonProcessingException {
        // given
        UUID userId = UUID.randomUUID();
        RoleUpdatedEvent event = new RoleUpdatedEvent(userId, "testuser", Role.USER, Role.ADMIN);
        String expectedPayload = "{\"userId\":\"" + userId + "\",\"oldRole\":\"USER\",\"newRole\":\"ADMIN\"}";

        given(objectMapper.writeValueAsString(event)).willReturn(expectedPayload);

        // when
        listener.on(event);

        // then
        then(kafkaTemplate).should().send(
            Topic.ROLE_UPDATED.getValue(),
            userId.toString(),
            expectedPayload
        );
    }

    @Test
    @DisplayName("BinaryContentUploadFailedEvent 발생 시 Kafka로 메시지 전송")
    void on_BinaryContentUploadFailedEvent_SendsToKafka() throws JsonProcessingException {
        // given
        UUID binaryContentId = UUID.randomUUID();
        String requestId = "test-request-id";
        String errorMessage = "Upload failed";
        BinaryContentUploadFailedEvent event = new BinaryContentUploadFailedEvent(
            binaryContentId, requestId, errorMessage);
        String expectedPayload = "{\"binaryContentId\":\"" + binaryContentId + "\"}";

        given(objectMapper.writeValueAsString(event)).willReturn(expectedPayload);

        // when
        listener.on(event);

        // then
        then(kafkaTemplate).should().send(
            Topic.UPLOAD_FAILED.getValue(),
            binaryContentId.toString(),
            expectedPayload
        );
    }

    @Test
    @DisplayName("JSON 직렬화 실패 시 Kafka로 메시지를 전송하지 않음")
    void on_JsonSerializationFails_DoesNotSendToKafka() throws JsonProcessingException {
        // given
        UUID messageId = UUID.randomUUID();
        MessageCreatedEvent event = new MessageCreatedEvent(messageId);

        given(objectMapper.writeValueAsString(event))
            .willThrow(new JsonProcessingException("Serialization error") {
            });

        // when
        listener.on(event);

        // then
        then(kafkaTemplate).should(never()).send(
            Topic.MESSAGE_CREATED.getValue(),
            messageId.toString(),
            null
        );
    }
}
