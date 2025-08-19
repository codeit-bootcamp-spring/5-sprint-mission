package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(path = "/publiccreate",
            method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublicChannel(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest){

        Channel channel = channelService.create(publicChannelCreateRequest);
        return ResponseEntity.ok(channel);

    }

    // 비공개 채널 생성
    @RequestMapping(path = "/privatecreate",
            method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest){

        Channel channel = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.ok(channel);

    }

    // 채널 업데이트
    @RequestMapping(path = "/update/{channelId}",
    method = RequestMethod.PUT)
    public ResponseEntity<Channel> updateChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest){
        Channel channel = channelService.update(channelId,publicChannelUpdateRequest);
        return ResponseEntity.ok(channel);
    }

    // 채널 삭제
    @RequestMapping(path = "/delete",
            method = RequestMethod.DELETE)
    public ResponseEntity<Channel> deleteChannel(@RequestParam UUID channelId){
        Channel channel = channelService.find(channelId);
        channelService.delete(channelId);
        return ResponseEntity.ok(channel);
    }

    // 채널 조회
    @RequestMapping(path = "/findAll",
            method = RequestMethod.GET)
    public ResponseEntity<List<Channel>> findAll(){
        List<Channel> channels = channelService.findAll();
        return ResponseEntity.ok(channels);
    }
}