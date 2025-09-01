package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.response.CreateMessageResponse;
import com.sprint.mission.discodeit.domain.response.MessageResponse;
import com.sprint.mission.discodeit.domain.response.MessagesInChannelResponse;
import com.sprint.mission.discodeit.domain.response.UpdateMessageResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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

	@Override
	@Transactional
	public Message create(CreateMessageDTO dto) {
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

		if (attachmentsInMessage != null && !attachmentsInMessage.isEmpty()) {
			attachmentsInMessage.forEach(binaryContentService::create);
		}

		return messageRepository.save(new Message(content, user, channel));
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		Message messageToDelete = messageRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));

		// 메시지 관련 Attachment 도 삭제
		// TODO
		// messageToDelete.getAttachmentIds().forEach(binaryContentRepository::delete);

		// 메시지 삭제
		messageRepository.deleteById(id);
	}

	@Override
	@Transactional
	public void deleteAll() {
		messageRepository.deleteAll();
	}

	@Override
	@Transactional
	public void deleteAllByChannelId(UUID channelId) {
		if (!channelRepository.existsById(channelId)) {
			throw new IllegalArgumentException("Channel ID cannot be null or empty");
		}
		messageRepository.deleteByChannelId(channelId);
	}

	@Override
	@Transactional
	public Message update(UpdateMessageDTO dto) {
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
			// TODO
			// List<UUID> newAttachmentIds = newFiles.stream()
			//   .map(BinaryContent::getId)
			//   .toList();
			// targetMessages.getAttachmentIds().addAll(newAttachmentIds);
		}

		return messageRepository.save(targetMessage);
	}

	@Override
	@Transactional(readOnly = true)
	public Message read(UUID id) {
		return messageRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Message> findAllByChannelId(UUID channelId) {
		return messageRepository.findAllByChannelId(channelId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmpty(UUID id) {
		return messageRepository.existsById(id);
	}

	@Override
	public List<UUID> findAttachmentsIds(UUID messageId) {
		return List.of();
	}

	public static CreateMessageResponse toCreateMessageResponse(Message newMessage) {
		return CreateMessageResponse.builder()
		  .id(newMessage.getId())
		  .createdAt(newMessage.getCreatedAt())
		  .updatedAt(newMessage.getUpdatedAt())
		  .content(newMessage.getContent())
		  .authorId(newMessage.getUser().getId())
		  .channelId(newMessage.getChannel().getId())
		  // .attachmentIds(newMessages.getAttachmentIds())
		  .build();
	}

	public static UpdateMessageResponse toUpdateMessageResponse(Message newMessage) {
		return UpdateMessageResponse.builder()
		  .id(newMessage.getId())
		  .createdAt(newMessage.getCreatedAt())
		  .updatedAt(newMessage.getUpdatedAt())
		  .content(newMessage.getContent())
		  .authorId(newMessage.getUser().getId())
		  .channelId(newMessage.getChannel().getId())
		  // .attachmentIds(newMessages.getAttachmentIds())
		  .build();
	}

	public static MessagesInChannelResponse toMessagesInChannelResponse(List<Message> messages) {
		return new MessagesInChannelResponse(
		  messages.stream().map(message ->
			new MessageResponse(
			  message.getId(),
			  message.getCreatedAt(),
			  message.getUpdatedAt(),
			  message.getContent(),
			  message.getUser().getId(),
			  message.getChannel().getId(),
			  null
			)
		  ).toList()
		);
	}
}
