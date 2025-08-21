package com.sprint.mission.discodeit.service.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.response.CreateMessageResponse;
import com.sprint.mission.discodeit.domain.response.MessageResponse;
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
	public Message create(CreateMessageDTO dto) {
		String content = dto.getContent();
		UUID channelId = dto.getChannelId();
		UUID userId = dto.getUserId();
		List<CreateBiContentDTO> attachmentsInMessage = dto.getAttachments();

		// Validate
		if (!channelRepository.existsById(channelId)) {
			throw new NoSuchElementException("channel with id " + channelId + "not found");
		}
		if (userRepository.isEmpty(userId)) {
			throw new NoSuchElementException("Author with id " + userId + "not found");

		}

		List<BinaryContent> files = new ArrayList<>();
		if (attachmentsInMessage != null && !attachmentsInMessage.isEmpty()) {
			attachmentsInMessage.forEach((file) -> {
				files.add(binaryContentService.create(file));
			});
		}

		List<UUID> attachmentIds = files.stream().map(BinaryContent::getId).toList();

		return messageRepository.save(new Message(content, userId, channelId, attachmentIds));
	}

	@Override
	public void delete(UUID id) {
		Message messageToDelete = messageRepository.find(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));

		// 메시지 관련 Attachment 도 삭제
		messageToDelete.getAttachmentIds().forEach(binaryContentRepository::delete);

		// 메시지 삭제
		messageRepository.delete(id);
	}

	@Override
	public void deleteAll() {
		messageRepository.deleteAll();
	}

	@Override
	public void deleteAllByChannelId(UUID channelId) {
		if (channelRepository.existsById(channelId)) {
			throw new IllegalArgumentException("Channel ID cannot be null or empty");
		}
		messageRepository.deleteByChannelId(channelId);
	}

	@Override
	public Message update(UpdateMessageDTO dto) {
		Optional.ofNullable(dto).orElseThrow(() -> new IllegalArgumentException("UpdateMessageDTO cannot be null"));
		UUID id = dto.getId();
		String newContent = dto.getNewContent();
		List<UUID> AttachmentIdsToRemove = dto.getRemoveAttachmentIds();
		List<CreateBiContentDTO> newAttachments = dto.getNewAttachments();

		if (newContent == null || newContent.isEmpty()) {
			throw new IllegalArgumentException("New content cannot be null or empty");
		}

		Message targetMessage = messageRepository.find(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));

		// 1. 내용 수정
		targetMessage.setContent(newContent);
		// 2. 삭제할 attachmentId가 있다면 삭제
		if (AttachmentIdsToRemove != null && !AttachmentIdsToRemove.isEmpty()) {
			// 기존 첨부파일 삭제
			AttachmentIdsToRemove.forEach(binaryContentRepository::delete);
			targetMessage.getAttachmentIds().removeAll(AttachmentIdsToRemove);
		}
		// 3. 새로 추가할 첨부파일이 있다면 추가
		if (newAttachments != null && !newAttachments.isEmpty()) {
			List<BinaryContent> newFiles = newAttachments.stream()
			  .map(binaryContentService::create)
			  .toList();
			List<UUID> newAttachmentIds = newFiles.stream()
			  .map(BinaryContent::getId)
			  .toList();
			targetMessage.getAttachmentIds().addAll(newAttachmentIds);
		}

		return messageRepository.save(targetMessage);
	}

	@Override
	public Message read(UUID id) {
		return messageRepository.find(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));
	}

	@Override
	public List<Message> findAllByChannelId(UUID channelId) {
		return messageRepository.findAll().stream().filter(
			message -> message.getChannelId().equals(channelId))
		  .toList();
	}

	@Override
	public List<Message> readAllByChannelId(UUID channelId) {
		return messageRepository.findAllByChannelId(channelId);
	}

	@Override
	public boolean isEmpty(UUID channelId) {
		return messageRepository.isEmpty(channelId);
	}

	public static CreateMessageResponse toCreateMessageResponse(Message newMessage) {
		return CreateMessageResponse.builder()
		  .id(newMessage.getId())
		  .createdAt(newMessage.getCreatedAt())
		  .updatedAt(newMessage.getUpdatedAt())
		  .content(newMessage.getContent())
		  .authorId(newMessage.getAuthorId())
		  .channelId(newMessage.getChannelId())
		  .attachmentIds(newMessage.getAttachmentIds())
		  .build();
	}

	public static UpdateMessageResponse toUpdateMessageResponse(Message newMessage) {
		return UpdateMessageResponse.builder()
		  .id(newMessage.getId())
		  .createdAt(newMessage.getCreatedAt())
		  .updatedAt(newMessage.getUpdatedAt())
		  .content(newMessage.getContent())
		  .authorId(newMessage.getAuthorId())
		  .channelId(newMessage.getChannelId())
		  .attachmentIds(newMessage.getAttachmentIds())
		  .build();
	}

	public static List<MessageResponse> toMessagesInChannelResponse(List<Message> messages) {
		return messages.stream().map(message ->
			MessageResponse.builder()
			  .id(message.getId())
			  .createdAt(message.getCreatedAt())
			  .updatedAt(message.getUpdatedAt())
			  .content(message.getContent())
			  .authorId(message.getAuthorId())
			  .channelId(message.getChannelId())
			  .attachmentIds(message.getAttachmentIds()).build())
		  .toList();
	}
}
