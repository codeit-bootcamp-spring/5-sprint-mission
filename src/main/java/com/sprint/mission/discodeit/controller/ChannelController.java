package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    @Qualifier("basicChannelService")
    private final ChannelService channelService;

    @Qualifier("basicMessageService")
    private final MessageService messageService;

    // 공개 채널 생성
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPublicChannel(@RequestBody ChannelPublicCreateRequest request) {
        ChannelResponse response = channelService.createPublicChannel(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPrivateChannel(@RequestBody ChannelPrivateCreateRequest request) {
        ChannelResponse response = channelService.createPrivateChannel(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 채널 정보 수정
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
    public ResponseEntity<ChannelResponse> updateChannel(@PathVariable UUID channelId, @RequestBody ChannelUpdateRequest request) {
        if (request.getId() == null || !channelId.equals(request.getId())) {
            throw new IllegalArgumentException("Channel ID in path does not match ID in request body.");
        }
        ChannelResponse response = channelService.update(request);
        return ResponseEntity.ok(response);
    }

    // 채널 삭제
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    // 특정 채널의 메시지 목록 조회
    @RequestMapping(value = "/{channelId}/messages", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> getMessagesInChannel(@PathVariable UUID channelId) {
        List<MessageResponse> messages = messageService.findMessagesByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }
}
