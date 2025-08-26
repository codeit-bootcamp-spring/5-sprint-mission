package com.sprint.mission.discodeit.controller;

import static com.sprint.mission.discodeit.service.basic.BasicMessageService.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.request.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.domain.response.CreateMessageResponse;
import com.sprint.mission.discodeit.domain.response.MessageResponse;
import com.sprint.mission.discodeit.domain.response.UpdateMessageResponse;
import com.sprint.mission.discodeit.service.MessageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {

	private final MessageService messageService;

	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CreateMessageResponse> createMessage(
	  @RequestPart MessageCreateRequest messageCreateRequest,
	  @RequestPart(required = false) List<MultipartFile> attachments) {

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
				  throw new RuntimeException("Error processing file: " + file.getOriginalFilename(), e);
			  }
		  }).toList();

		Message newMessage = messageService.create(CreateMessageDTO.builder()
		  .content(messageCreateRequest.getContent())
		  .channelId(messageCreateRequest.getChannelId())
		  .userId(messageCreateRequest.getAuthorId())
		  .attachments(biContentDTOs)
		  .build());

		URI location = URI.create("api/messages");
		return ResponseEntity.created(location).body(toCreateMessageResponse(newMessage));
	}

	@PatchMapping
	public ResponseEntity<UpdateMessageResponse> updateMessage(
	  @RequestParam UUID messageId,
	  @RequestBody @Valid UpdateMessageRequest updateMessageRequest
	) {

		List<CreateBiContentDTO> biContentDTOs = new ArrayList<>();

		Message updatedMessage = messageService.update(UpdateMessageDTO.builder()
		  .id(messageId)
		  .newContent(updateMessageRequest.getNewContent())
		  .newAttachments(biContentDTOs)
		  .build());

		return ResponseEntity.ok().body(toUpdateMessageResponse(updatedMessage));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
		messageService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<MessageResponse>> GetMessagesInChannel(@RequestParam UUID channelId) {
		List<Message> readMessages = messageService.readAllByChannelId(channelId);
		return ResponseEntity.ok((toMessagesInChannelResponse(readMessages)));
	}
}
