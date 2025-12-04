package com.sprint.mission.discodeit.event.handler;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {
	private final ObjectMapper objectMapper;
	private final NotificationService notificationService;
	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;

	@KafkaListener(topics = "discodeit.MessageCreatedEvent")
	public void onMessageCreatedEvent(String kafkaEvent) throws JsonProcessingException {
		MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
		log.info("[Kafka] MessageCreatedEvent received: {}", event);
		String title = event.authorName() + " (#" + ((event.channelName() == null)
			? "Private Channel" : event.channelName()) + ")";
		String content = event.content();

		List<NotificationCreateRequest> requests = readStatusRepository.findAllByChannelId(event.channelId())
			.stream()
			.filter(ReadStatus::isNotificationEnabled)
			.map(rs -> rs.getUser().getId())
			.filter(id -> !id.equals(event.authorId()))
			.map(id -> new NotificationCreateRequest(id, title, content))
			.toList();

		notificationService.createAll(requests);
	}

	@KafkaListener(topics = "discodeit.RoleUpdatedEvent")
	public void onRoleUpdatedEvent(String kafkaEvent) throws JsonProcessingException {
		RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
		log.info("[Kafka] RoleUpdatedEvent received: {}", event);
		String title = "권한이 변경되었습니다.";
		String content = event.oldRole() + " -> " + event.newRole();
		notificationService.create(
			new NotificationCreateRequest(event.userId(), title, content)
		);
	}

	@KafkaListener(topics = "discodeit.S3UploadFailedEvent")
	public void onS3UploadFailedEvent(String kafkaEvent) throws JsonProcessingException {
		S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
		log.info("[Kafka] S3UploadFailedEvent received: {}", event);
		String title = "S3 파일 업로드 실패";
		String content = "RequestId: " + event.requestId() + "\n"
			+ "BinaryContentId: " + event.binaryContentId() + "\n"
			+ "Error: " + event.errorMessage();
		List<NotificationCreateRequest> requests = userRepository.findByRole(Role.ADMIN).stream()
			.map(User::getId)
			.map(id -> new NotificationCreateRequest(id, title, content))
			.toList();
		notificationService.createAll(requests);
	}

}
