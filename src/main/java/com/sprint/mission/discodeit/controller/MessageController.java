package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    //메시지 전송
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<MessageDto>> messageSend(@RequestPart("message") MessageCreateRequest messageCreateRequest,
                                                             @RequestPart(required = false, value = "attachment") List<MultipartFile> attachment) throws IOException {
        List<BinaryContentCreateRequest> binaryContent = List.of();

        if (attachment != null && !attachment.isEmpty()) {
            binaryContent = attachment.stream()
                    .map(file -> {
                        try {
                            return new BinaryContentCreateRequest(
                                    file.getOriginalFilename(), file.getContentType(), file.getBytes()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }

        MessageDto message = messageService.create(messageCreateRequest, binaryContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(message, "메시지가 전송되었습니다"));
    }

    //메시지 수정
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<MessageDto>> messageUpdate(@PathVariable("id") UUID id,
                                                               @RequestBody MessageUpdateRequest messageUpdateRequest) {
        MessageDto message = messageService.update(id, messageUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(message, "메시지 " + id + "가 수정되었습니다"));
    }

    //메시지 삭제
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> messageDelete(@PathVariable("id") UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //메시지 조회
    @GetMapping
    public ResponseEntity<ApiResult<List<MessageDto>>> messageFindByChannelId(@RequestParam("channelId") UUID id) {
        List<MessageDto> messages = messageService.findAllByChannelId(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(messages));
    }
}
