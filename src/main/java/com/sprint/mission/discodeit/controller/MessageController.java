package com.sprint.mission.discodeit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageDeleteRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
	private final MessageService messageService;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MessageResponse> createMessage(
		@RequestPart("message") MessageCreateRequest request,
		@RequestPart(value = "attachments", required = false) List<MultipartFile> files
	) {
		try {
			if (files != null && !files.isEmpty()) {
				List<BinaryContentCreateRequest> attachments = new ArrayList<>();
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						BinaryContentCreateRequest attachment = BinaryContentCreateRequest.builder()
							.fileName(file.getOriginalFilename())
							.contentType(file.getContentType())
							.size(file.getSize())
							.bytes(file.getBytes())
							.build();
						attachments.add(attachment);
					}
				}
				request.setAttachments(attachments);
			}

			MessageResponse response = messageService.createMessage(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IOException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<MessageResponse>> getMessagesByChannel(@ModelAttribute UUID channelId) {
		List<MessageResponse> messages = messageService.findMessagesByChannelId(channelId);
		return ResponseEntity.ok(messages);
	}

	@RequestMapping(path = "/update", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MessageResponse> updateMessage(
		@RequestPart("message") MessageUpdateRequest request,
		@RequestPart(value = "attachments", required = false) List<MultipartFile> files
	) {
		try {
			if (files != null && !files.isEmpty()) {
				List<BinaryContentCreateRequest> attachments = new ArrayList<>();
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						BinaryContentCreateRequest attachment = BinaryContentCreateRequest.builder()
							.fileName(file.getOriginalFilename())
							.contentType(file.getContentType())
							.size(file.getSize())
							.bytes(file.getBytes())
							.build();
						attachments.add(attachment);
					}
				}
				request.setAttachmentsToAdd(attachments);
			}

			MessageResponse response = messageService.updateMessage(request);
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@RequestMapping(path = "/delete", method = RequestMethod.DELETE)
	public ResponseEntity<MessageDeleteResponse> deleteMessage(@RequestBody MessageDeleteRequest request) {
		MessageDeleteResponse response = messageService.deleteMessage(request);
		return ResponseEntity.ok(response);
	}
}