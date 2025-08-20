package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/message")
public class MessageController {

    private final MessageService messageService;

    // 메시지를 보낼 수 있다
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse.detail> create(@Valid @ModelAttribute MessageRequest.create dto) {
        return ResponseEntity.ok(messageService.create(dto));
    }

    // 메시지를 수정할 수 있다
    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse.updated> update(@Valid @ModelAttribute MessageRequest.update dto) {
        return ResponseEntity.ok(messageService.update(dto));
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
    @RequestMapping(value = "find/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findByChannel(@PathVariable UUID id) {
        List<Message> messageList = messageService.findByChannel(id);

        return ResponseEntity.ok(messageList);
    }

}
