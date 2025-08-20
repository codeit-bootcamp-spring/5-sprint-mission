package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
                        }catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }

        Message message = messageService.create(messageCreateRequest, binaryContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(message, "메시지가 전송되었습니다"));
    }
}
