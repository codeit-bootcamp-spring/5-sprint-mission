package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.message.MessageDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final BasicBinaryContentService binaryContentService;
	private final MessageMapper messageMapper;
	private final UserStatusRepository userStatusRepository;

	@Override
	@Transactional
	public MessageDto create(CreateMessageDTO dto) {
		String content = dto.getContent();
		UUID channelId = dto.getChannelId();
		UUID userId = dto.getUserId();
		List<CreateBiContentDTO> attachmentsInMessage = dto.getAttachments();

		// Validate
		Channel channel = channelRepository.findById(channelId).orElseThrow(() ->
		  new NoSuchElementException("channel with id " + channelId + "not found")
		);

		User user = userRepository.findById(userId).orElseThrow(() ->
		  new NoSuchElementException("Author with id " + userId + "not found")
		);

		boolean isOnline = userStatusRepository.findByUserId(user.getId())
		  .map(UserStatus::isOnline).orElse(false);

		List<BinaryContent> attachments = Optional.ofNullable(attachmentsInMessage)
		  .map(a -> a.stream().map(binaryContentService::create).toList())
		  .orElse(List.of());

		Message savedMessage = messageRepository.save(new Message(content, user, channel, attachments));

		return messageMapper.toDto(savedMessage, user, isOnline);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		Message messageToDelete = messageRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));

		// 메시지 관련 Attachment 도 삭제
		binaryContentRepository.deleteByIdIn(
		  messageToDelete.getAttachments().stream().map(BinaryContent::getId).toList());

		// 메시지 삭제
		messageRepository.deleteById(id);
	}

	@Override
	@Transactional
	public MessageDto update(UpdateMessageDTO dto) {
		Optional.ofNullable(dto).orElseThrow(() -> new IllegalArgumentException("UpdateMessageDTO cannot be null"));
		UUID id = dto.getId();
		String newContent = dto.getNewContent();
		List<UUID> AttachmentIdsToRemove = dto.getRemoveAttachmentIds();
		List<CreateBiContentDTO> newAttachments = dto.getNewAttachments();

		if (newContent == null || newContent.isEmpty()) {
			throw new IllegalArgumentException("New content cannot be null or empty");
		}

		Message targetMessage = messageRepository.findById(id).orElseThrow(
		  () -> new NoSuchElementException("Message with ID " + id + " not found"));

		// 1. 내용 수정
		targetMessage.setContent(newContent);
		// 2. 삭제할 attachmentId가 있다면 삭제
		if (AttachmentIdsToRemove != null && !AttachmentIdsToRemove.isEmpty()) {
			// 기존 첨부파일 삭제
			AttachmentIdsToRemove.forEach(binaryContentRepository::deleteById);
			// targetMessages.getAttachmentIds().removeAll(AttachmentIdsToRemove);
		}
		// 3. 새로 추가할 첨부파일이 있다면 추가
		if (newAttachments != null && !newAttachments.isEmpty()) {
			List<BinaryContent> newFiles = newAttachments.stream()
			  .map(binaryContentService::create)
			  .toList();
			targetMessage.getAttachments().addAll(newFiles);
		}

		// TODO N+1
		// 4. User 정보 가져오기
		boolean isOnline = userStatusRepository.findByUserId(targetMessage.getUser().getId())
		  .map(UserStatus::isOnline)
		  .orElse(false);

		messageRepository.save(targetMessage);
		return messageMapper.toDto(targetMessage, targetMessage.getUser(), isOnline);
	}

	@Override
	@Transactional(readOnly = true)
	// TODO N+1
	public MessageDto read(UUID id) {
		Message message = messageRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));

		boolean isOnline = userStatusRepository.findByUserId(message.getUser().getId())
		  .map(UserStatus::isOnline)
		  .orElse(false);

		return messageMapper.toDto(message, message.getUser(), isOnline);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
		Page<Message> messages = messageRepository.findAllByChannelId(channelId, pageable);

		// TODO N+1
		return messageRepository.findAllByChannelId(channelId, pageable).map(message ->
		  messageMapper.toDto(
			message,
			message.getUser(),
			userStatusRepository.findByUserId(message.getUser().getId())
			  .map(UserStatus::isOnline)
			  .orElse(false)
		  )
		);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmpty(UUID id) {
		return messageRepository.existsById(id);
	}

}
