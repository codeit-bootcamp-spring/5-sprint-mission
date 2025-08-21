package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/create/public", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<Channel>> publicChannelCreate(@RequestBody PublicChannelCreateRequest createRequest) {
        Channel channel = channelService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(channel, "공개 채널이 생성되었습니다"));
    }

    @RequestMapping(value = "/create/private", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<Channel>> privateChannelCreate(@RequestBody PrivateChannelCreateRequest createRequest) {
        Channel channel = channelService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(channel, "비공개 채널이 생성되었습니다"));
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<Channel>> channelUpdate(@PathVariable("id") UUID id,
                                                            @RequestBody PublicChannelUpdateRequest updateRequest) {
        Channel channel = channelService.update(id, updateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(channel, "채널 " + channel.getId() + "이 수정되었습니다"));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ApiResult<Channel>> channelDelete(@PathVariable("id") UUID id) {
        channelService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok("채널 " + id + "이 삭제되었습니다"));
    }

    @RequestMapping(value = "find/{userId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<ChannelDto>>> channelFindAllByUserId(@PathVariable("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(channels));
    }
}
