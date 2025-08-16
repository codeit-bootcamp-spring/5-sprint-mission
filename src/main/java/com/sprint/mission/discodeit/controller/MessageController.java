package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    // [POST] 메시지 보내기 (multipart: messageCreateRequest JSON + files[])
    @RequestMapping(path = "send", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> send(
            @RequestPart("messageCreateRequest") MessageCreateRequest req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        List<BinaryContentCreateRequest> binaryReqs = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                if (f != null && !f.isEmpty()) {
                    binaryReqs.add(new BinaryContentCreateRequest(
                            f.getOriginalFilename(),
                            f.getContentType(),
                            f.getBytes()
                    ));
                }
            }
        }
        Message created = messageService.create(req, binaryReqs);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // [PUT] 메시지 수정
    @RequestMapping(path = "update/{messageId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> update(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest req
    ) {
        Message updated = messageService.update(messageId, req);
        return ResponseEntity.ok(updated);
    }

    // [DELETE] 메시지 삭제
    @RequestMapping(path = "delete/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    // [GET] 특정 채널의 메시지 목록 조회
    @RequestMapping(path = "list/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> listByChannel(@PathVariable("channelId") UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
