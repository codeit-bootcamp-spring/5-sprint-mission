package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
