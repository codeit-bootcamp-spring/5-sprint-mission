package com.sprint.mission.discodeit.Controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    // 메시지 전송
    @RequestMapping(value = "/send", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> sendMessage(
            @RequestPart MessageCreateRequest messageCreateRequest,
            @RequestPart(required = false) List<MultipartFile> profiles
    ) throws IOException {
        List<BinaryContentCreateRequest> files = new ArrayList<>();
        if (profiles != null) {
            for (MultipartFile profile : profiles) {
                files.add(new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                ));
            }
        }
        Message created = messageService.create(messageCreateRequest, files);
        return ResponseEntity.ok(created);
    }

    // 메시지 수정
    @RequestMapping(value = "/update/{messageId}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        Message updated = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.ok(updated);
    }

    // 메시지 조회
    @RequestMapping(value = "/find/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findMessages(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }
}
