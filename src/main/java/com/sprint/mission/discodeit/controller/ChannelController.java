package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final ReadStatusService readStatusService;

    @PostMapping("/public")
    public ResponseEntity<ChannelResponse.Detail> createChannel(@Valid @RequestBody ChannelRequest.Create dto) {
        Channel channel = channelService.create(dto.name(), dto.description());
        return ResponseEntity.ok(ChannelResponse.Detail.from(channel));
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponse.Detail> createPrivateChannel(@Valid @RequestBody List<UUID> participantIds) {
        Channel channel = channelService.createPrivate(participantIds);
        return ResponseEntity.ok(ChannelResponse.Detail.from(channel));
    }

    @PatchMapping(params = "channelId")
    public ResponseEntity<ChannelResponse.Detail> updateChannel(
            @RequestParam UUID channelId,
            @Valid
            @RequestBody ChannelRequest.Update req) {
        Channel channel = channelService.update(channelId, req.newName(), req.newDescription());
        return ResponseEntity.ok(ChannelResponse.Detail.from(channel));
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<ChannelResponse.JoinedChannels>> findByUser(@RequestParam UUID userId) {

        // 유저가 참여하는 모든 채널
        List<Channel> channelList = channelService.findByUser(userId);

        // 채널별 DTO 생성
        List<ChannelResponse.JoinedChannels> response = channelList.stream()
                .map(channel -> {
                    // 유저의 채널별 ReadStatus (Optional → orElse(null) 처리)
                    ReadStatus readStatus = readStatusService
                            .findByUserIdAndChannelId(userId, channel.getId());

                    // 채널 참여자 목록
                    List<UUID> participantIds = readStatusService.findAllUsers(channel.getId());

                    // DTO 변환
                    return ChannelResponse.JoinedChannels.from(channel, readStatus, participantIds);
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<String> joinChannel(@ModelAttribute ChannelRequest.join dto) {
        Channel channel = channelService.join(dto.userId(), dto.channelId());
        return ResponseEntity.ok(dto.userId() + " 님이 " + channel.getName() + "에 참여합니다.");
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<Channel>> findAll() {
        return ResponseEntity.ok(channelService.findAll());
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
