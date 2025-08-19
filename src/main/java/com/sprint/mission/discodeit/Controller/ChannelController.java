package com.sprint.mission.discodeit.Controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(value = "/create/public", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Channel> createChannel(@RequestBody PublicChannelCreateRequest publicChannel) {
        Channel created = channelService.create(publicChannel);
        return ResponseEntity.ok(created);
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/create/private", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Channel> createChannel(@RequestBody PrivateChannelCreateRequest privateChannel) {
        Channel created = channelService.create(privateChannel);
        return ResponseEntity.ok(created);
    }

    // 공개 채널 수정
    @RequestMapping(value = "/update/{channelId}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Channel> updateChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannel) {
        Channel updated = channelService.update(channelId, publicChannel);
        return ResponseEntity.ok(updated);
    }

    // 채널 삭제
    @RequestMapping(value = "/delete/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    // 채널 목록 조회
    @RequestMapping(value = "/find/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllChannels(@PathVariable UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }




}
