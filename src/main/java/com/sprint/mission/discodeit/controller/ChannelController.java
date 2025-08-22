package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    //공개 채널 생성
    @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<ChannelDto>> publicChannelCreate(@RequestBody PublicChannelCreateRequest createRequest) {
        ChannelDto channel = channelService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(channel, "공개 채널이 생성되었습니다"));
    }

    //비공개 채널 생성
    @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<ChannelDto>> privateChannelCreate(@RequestBody PrivateChannelCreateRequest createRequest) {
        ChannelDto channel = channelService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(channel, "비공개 채널이 생성되었습니다"));
    }

    //채널 정보 수정
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<ChannelDto>> channelUpdate(@PathVariable("id") UUID id,
                                                               @RequestBody PublicChannelUpdateRequest updateRequest) {
        ChannelDto channel = channelService.update(id, updateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(channel, "채널 " + channel.id() + "이 수정되었습니다"));
    }

    //채널 삭제
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> channelDelete(@PathVariable("id") UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //채널 목록 조회
    @GetMapping
    public ResponseEntity<ApiResult<List<ChannelDto>>> channelFindAllByUserId(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(channels));
    }
}
