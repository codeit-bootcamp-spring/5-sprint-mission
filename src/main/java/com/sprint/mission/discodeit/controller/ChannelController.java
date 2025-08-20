package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // ✅ 공개 채널 생성
    @RequestMapping(value = "/createPublic", method = RequestMethod.POST)
    public ResponseEntity<Void> createPublic(@RequestBody PublicChannelCreateRequest request) {
        channelService.create(request.toEntity()); // Entity로 변환해서 넘김
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // ✅ 비공개 채널 생성
    @RequestMapping(value = "/createPrivate", method = RequestMethod.POST)
    public ResponseEntity<Void> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        channelService.create(request.toEntity()); // Entity로 변환해서 넘김
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // ✅ 채널 단건 조회
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Channel> findById(@PathVariable UUID id) {
        Channel channel = channelService.findById(id);
        return ResponseEntity.ok(channel);
    }

    // ✅ 전체 채널 조회
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<Channel>> findAll() {
        return ResponseEntity.ok(channelService.findAll());
    }

    // ✅ 채널 수정
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody Channel updated) {
        channelService.update(updated);
        return ResponseEntity.ok().build();
    }

    // ✅ 채널 삭제
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
