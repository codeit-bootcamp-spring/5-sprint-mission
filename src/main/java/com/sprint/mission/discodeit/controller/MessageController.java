package com.sprint.mission.discodeit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.sprint.mission.discodeit.dto.request.binaryContent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.DeleteMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessagesByChannelIdRequest;
import com.sprint.mission.discodeit.dto.request.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.message.DeleteMessageResponse;
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
		@RequestPart("message") CreateMessageRequest request,
		@RequestPart(value = "attachments", required = false) List<MultipartFile> files
	) {
		try {
			if (files != null && !files.isEmpty()) {
				List<CreateBinaryContentRequest> attachments = new ArrayList<>();
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						CreateBinaryContentRequest attachment = CreateBinaryContentRequest.builder()
							.filename(file.getOriginalFilename())
							.contentType(file.getContentType())
							.size(file.getSize())
							.content(file.getBytes())
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
	public ResponseEntity<List<MessageResponse>> getMessagesByChannel(@ModelAttribute GetMessagesByChannelIdRequest request) {
		List<MessageResponse> messages = messageService.getAllByChannelId(request);
		return ResponseEntity.ok(messages);
	}

	@RequestMapping(path = "/update", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MessageResponse> updateMessage(
		@RequestPart("message") UpdateMessageRequest request,
		@RequestPart(value = "attachments", required = false) List<MultipartFile> files
	) {
		try {
			if (files != null && !files.isEmpty()) {
				List<CreateBinaryContentRequest> attachments = new ArrayList<>();
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						CreateBinaryContentRequest attachment = CreateBinaryContentRequest.builder()
							.filename(file.getOriginalFilename())
							.contentType(file.getContentType())
							.size(file.getSize())
							.content(file.getBytes())
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
	public ResponseEntity<DeleteMessageResponse> deleteMessage(@RequestBody DeleteMessageRequest request) {
		DeleteMessageResponse response = messageService.deleteMessage(request);
		return ResponseEntity.ok(response);
	}
}