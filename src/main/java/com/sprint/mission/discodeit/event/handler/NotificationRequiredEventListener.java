package com.sprint.mission.discodeit.event.handler;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
	private final NotificationService notificationService;
	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;

	@Async("taskExecutor")
	// @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void on(MessageCreatedEvent event) {
		log.info("MessageCreatedEvent received: {}", event);
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

	@Async("taskExecutor")
	// @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void on(RoleUpdatedEvent event) {
		String title = "권한이 변경되었습니다.";
		String content = event.oldRole() + " -> " + event.newRole();
		notificationService.create(
			new NotificationCreateRequest(event.userId(), title, content)
		);
	}

	@Async("taskExecutor")
	// @EventListener
	public void on(S3UploadFailedEvent event) {
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
