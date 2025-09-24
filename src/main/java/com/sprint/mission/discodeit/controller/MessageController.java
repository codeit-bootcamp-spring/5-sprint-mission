package com.sprint.mission.discodeit.controller;

import static org.springframework.http.MediaType.*;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.message.MessageDto;
import com.sprint.mission.discodeit.domain.dto.message.MessageResponse;
import com.sprint.mission.discodeit.domain.request.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.domain.response.PageResponse;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
@Slf4j
public class MessageController {

	private final MessageService messageService;
	private final PageResponseMapper pageResponseMapper;
	private final MessageMapper messageMapper;

	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MessageResponse> createMessage(
	  @RequestPart @Valid MessageCreateRequest messageCreateRequest,
	  @RequestPart(required = false) List<MultipartFile> attachments) {
		log.debug("Request to create message started channelID={}, authorID={}, content={}",
		  messageCreateRequest.getChannelId(), messageCreateRequest.getAuthorId(), messageCreateRequest.getContent());

		List<CreateBiContentDTO> biContentDTOs = Optional.ofNullable(attachments).orElseGet(List::of).stream()
		  .map(file -> {
			  try {
				  return new CreateBiContentDTO(
					file.getBytes(),
					file.getSize(),
					file.getContentType(),
					file.getOriginalFilename()
				  );
			  } catch (IOException e) {
				  log.error("Error Read attachment files in Message channelID={}, authorID={}, content={}",
					messageCreateRequest.getChannelId(),
					messageCreateRequest.getAuthorId(),
					messageCreateRequest.getContent());
				  throw new RuntimeException("Error processing file: " + file.getOriginalFilename(), e);
			  }
		  }).toList();

		MessageDto newMessage = messageService.create(CreateMessageDTO.builder()
		  .content(messageCreateRequest.getContent())
		  .channelId(messageCreateRequest.getChannelId())
		  .userId(messageCreateRequest.getAuthorId())
		  .attachments(biContentDTOs)
		  .build());

		URI location = URI.create("api/messages");
		log.debug("URI location={} in messageID={}", location, newMessage.getId());

		log.debug("message created Request successfully done channelID={}, authorID={}, content={}",
		  messageCreateRequest.getChannelId(), messageCreateRequest.getAuthorId(), messageCreateRequest.getContent());
		return ResponseEntity.created(location).body(messageMapper.toResponse(newMessage));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<MessageResponse> updateMessage(
	  @PathVariable UUID id,
	  @RequestBody @Valid UpdateMessageRequest updateMessageRequest
	) {
		log.debug("Request to update user started MessageID={}", id);

		MessageDto updatedMessage = messageService.update(UpdateMessageDTO.builder()
		  .id(id)
		  .newContent(updateMessageRequest.getNewContent())
		  .build());

		log.debug("Message updated Request successfully done MessageID={}", id);
		return ResponseEntity.ok().body(messageMapper.toResponse(updatedMessage));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
		log.debug("Request to delete Message started messageID={}", id);

		messageService.delete(id);
		log.debug("Message delete Request successfully done messageID={}", id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<PageResponse<MessageResponse>> getMessagesInChannel(
	  @RequestParam UUID channelId,
	  @RequestParam(required = false) Instant cursor,
	  @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		if (cursor != null) {
			Slice<MessageResponse> readMessages = messageService.findAllCursorByChannelId(channelId, cursor, pageable)
			  .map(messageMapper::toResponse);
			readMessages.forEach(m -> System.out.println(m.getAuthor()));
			return ResponseEntity.ok((pageResponseMapper.fromSlice(readMessages)));
		} else {
			Page<MessageResponse> readMessages = messageService.findAllByChannelId(channelId, pageable).map(
			  messageMapper::toResponse);
			readMessages.forEach(m -> System.out.println(m.getAuthor()));

			return ResponseEntity.ok((pageResponseMapper.fromPage(readMessages)));
		}
	}
}
