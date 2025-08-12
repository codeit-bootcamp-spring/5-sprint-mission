package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<ChannelResponse> createChannel(@Valid @ModelAttribute ChannelCreateDto dto) {
        Channel channel = channelService.create(dto);
        return ResponseEntity.ok(new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getTopic(),
                channel.getDescription(),
                channel.getCreatedAtFormatted(),
                channel.getUpdatedAtFormatted()
        ));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<ChannelResponse> updateChannel(@Valid @ModelAttribute ChannelUpdateRequest req) {
        ChannelResponse channel = channelService.update(req);
        return ResponseEntity.ok(channel);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String,String>> delete(@PathVariable UUID id) {
        boolean deleted = channelService.delete(id);
        return deleted
                ? ResponseEntity.ok(Map.of("message", "채널이 삭제되었습니다."))
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "채널을 찾을 수 없습니다."));
    }
}
