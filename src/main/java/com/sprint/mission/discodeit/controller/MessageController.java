package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
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
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(value = "/send", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<Message>> messageSend(@RequestPart("message") MessageCreateRequest messageCreateRequest,
                                                          @RequestPart(required = false, value = "attachment") List<MultipartFile> attachment) throws IOException {
        List<BinaryContentCreateRequest> binaryContent = List.of();

        if (attachment != null && !attachment.isEmpty()) {
            binaryContent = attachment.stream()
                    .map(file -> {
                        try {
                            return new BinaryContentCreateRequest(
                                    file.getName(), file.getContentType(), file.getBytes()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }

        Message message = messageService.create(messageCreateRequest, binaryContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(message, "메시지가 전송되었습니다"));
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<Message>> messageUpdate(@PathVariable("id") UUID id,
                                                            @RequestBody MessageUpdateRequest messageUpdateRequest) {
        Message message = messageService.update(id, messageUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(message, "메시지 " + id + "가 수정되었습니다"));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ApiResult<Message>> messageUpdate(@PathVariable("id") UUID id) {
        messageService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok("메시지 " + id + "가 삭제되었습니다"));
    }

    @RequestMapping(value = "/find/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<Message>>> messageFindByChannelId(@PathVariable("channelId") UUID id){
        List<Message> messages = messageService.findAllByChannelId(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(messages));
    }
}
