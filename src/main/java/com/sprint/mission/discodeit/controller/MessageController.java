package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.FileDto;
import com.sprint.mission.discodeit.dto.message.MessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
@RequestMapping("api/messages")
public class MessageController {

    private final MessageService messageService;

    // 메시지를 보낼 수 있다
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse.Detail> create(
        @Valid
        @RequestPart("messageCreateRequest") MessageRequest.Create req) throws IOException {
            List<FileDto> fileList = new ArrayList<>();

            if (req.attachments() != null && !req.attachments().isEmpty()) {

                fileList = req.attachments().stream().map(file -> {
                            try {
                                return new FileDto(UUID.randomUUID().toString(), file.getContentType(), file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException("파일 변환 실패", e);
                            }
                        }).toList();
            }
            Message message = messageService.create(req.authorId(), req.channelId(), req.content(), fileList);

            return ResponseEntity.ok(MessageResponse.Detail.of(message));
    }

    // 메시지를 수정할 수 있다
    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse.Detail> update(@Valid @ModelAttribute MessageRequest.update dto) {
        Message message = messageService.update(dto);
        return ResponseEntity.ok(MessageResponse.Detail.of(message));
    }

    //메시지를 삭제할 수 있다
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String,String>> delete(@PathVariable UUID id) {
        boolean deleted = messageService.delete(id);
        return deleted
                ? ResponseEntity.ok(Map.of("message", "메시지가 삭제되었습니다."))
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "메시지를 찾을 수 없습니다."));
    }

    /**
     * [API 요구사항] 특정 채널의 메시지 목록을 조회할 수 있다
     */
    @GetMapping(params = "channelId")
    public ResponseEntity<List<Message>> findByChannel(@RequestParam UUID channelId) {
        List<Message> messageList = messageService.findByChannel(channelId);

        return ResponseEntity.ok(messageList);
    }

}
