package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.controller.api.MessageApi;
import com.codeit.mission.discodeit.dto.data.MessageDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageUpdateRequest;
import com.codeit.mission.discodeit.dto.response.PageResponse;
import com.codeit.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        log.info("메시지 생성 API 호출 - channelId: {}, authorId: {}, 내용 길이: {} 글자, 첨부파일 수: {}",
                messageCreateRequest.channelId(), messageCreateRequest.authorId(),
                messageCreateRequest.content().length(), attachments.size());

        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequest(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList())
                .orElse(new ArrayList<>());
        MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
        log.info("메시지 생성 API 성공 - messageId: {}, channelId: {}, 첨부파일 수: {}",
                createdMessage.id(), messageCreateRequest.channelId(), attachments.size());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessage);
    }

    @PatchMapping(path = "{messageId}")
    public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request) {
        log.info("메시지 수정 API 호출 - messageId: {}, newContent: {}", messageId, request.newContent());

        MessageDto updatedMessage = messageService.update(messageId, request);
        log.info("메시지 수정 API 성공 - messageId: {}", messageId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedMessage);
    }

    @DeleteMapping(path = "{messageId}")
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        log.info("메시지 삭제 API 호출 - messageId: {}", messageId);
        messageService.delete(messageId);
        log.info("메시지 삭제 API 성공 - messageId: {}", messageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId,
            @RequestParam(value = "cursor", required = false) Instant cursor,
            @PageableDefault(
                    size = 50,
                    page = 0,
                    sort = "createdAt",
                    direction = Direction.DESC
            ) Pageable pageable) {
        log.debug("채널별 메시지 목록 조회 API 호출 - channelId: {}, cursor: {}, pageSize: {}",
                channelId, cursor, pageable.getPageSize());
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor,
                pageable);
        log.info("채널별 메시지 목록 조회 API 성공 - channelId: {}, 조회된 메시지 수: {}, hasNext: {}",
                channelId, messages.content().size(), messages.hasNext());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messages);
    }
}
