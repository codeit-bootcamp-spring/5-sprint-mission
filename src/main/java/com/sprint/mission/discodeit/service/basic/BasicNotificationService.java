package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@CacheEvict(value = "notifications", key = "#request.receiverId()")
	public void create(NotificationCreateRequest request) {
		log.debug("Creating notification: {}", request);
		Notification notification = new Notification(
			request.receiverId(),
			request.title(),
			request.content()
		);
		notificationRepository.save(notification);
		log.info("Created notification: {}", notification);
	}

	@Override
	@CacheEvict(value = "notifications", allEntries = true)
	public void createAll(List<NotificationCreateRequest> requests) {
		log.debug("Creating notifications: {}", requests);
		List<Notification> notifications = requests.stream()
			.map(request -> new Notification(
				request.receiverId(),
				request.title(),
				request.content()
			))
			.toList();
		notificationRepository.saveAll(notifications);
		log.info("Created notifications: {}", notifications);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "notifications", key = "#userId")
	public List<NotificationDto> findByUserId(UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException().addDetail("userId", userId);
		}
		return notificationRepository.findAllByReceiverId(userId);
	}

	@Transactional(readOnly = true)
	public boolean isOwner(UUID notificationId, UUID userId) {
		return notificationRepository.findById(notificationId)
			.map(Notification::getReceiverId)
			.filter(userId::equals)
			.isPresent();
	}

	@Override
	@Transactional
	@PreAuthorize("@basicNotificationService.isOwner(#id, principal.userDto.id)")
	@CacheEvict(value = "notifications", key = "principal.userDto.id")
	public void delete(UUID id) {
		if (!notificationRepository.existsById(id)) {
			throw new DiscodeitException(ErrorCode.NOTIFICATION_NOT_FOUND).addDetail("notificationId", id);
		}
		notificationRepository.deleteById(id);
	}
}
