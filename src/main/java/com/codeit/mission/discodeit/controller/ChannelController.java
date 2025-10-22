package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.controller.api.ChannelApi;
import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.codeit.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping(path = "public")
    public ResponseEntity<ChannelDto> create(
            @Valid @RequestBody PublicChannelCreateRequest request) {
        log.info("공개 채널 생성 API 호출 - name: {}, description: {}",
                request.name(), request.description());

        ChannelDto createdChannel = channelService.create(request);
        log.info("퍼블릭 채널 생성 API 성공 - channelId: {}, name: {}",
                createdChannel.id(), request.name());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PostMapping(path = "private")
    public ResponseEntity<ChannelDto> create(
            @Valid @RequestBody PrivateChannelCreateRequest request) {
        log.info("프라이빗 채널 생성 API 호출 - 참가자 수: {}", request.participantIds().size());
        log.debug("프라이빗 채널 참가자 목록 - participantIds: {}", request.participantIds());

        ChannelDto createdChannel = channelService.create(request);
        log.info("프라이빗 채널 생성 API 성공 - channelId: {}, 참가자 수: {}",
                createdChannel.id(), request.participantIds().size());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PatchMapping(path = "{channelId}")
    public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request) {
        log.info("채널 수정 API 호출 - channelId: {}, newName: {}, newDescription: {}",
                channelId, request.newName(), request.newDescription());

        ChannelDto updatedChannel = channelService.update(channelId, request);
        log.info("채널 수정 API 성공 - channelId: {}, newName: {}", channelId, request.newName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedChannel);
    }

    @DeleteMapping(path = "{channelId}")
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        log.info("채널 삭제 API 호출 - channelId: {}", channelId);
        channelService.delete(channelId);
        log.info("채널 삭제 API 성공 - channelId: {}", channelId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        log.debug("사용자별 채널 목록 조회 API 호출 - userId: {}", userId);
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        log.info("사용자별 채널 목록 조회 API 성공 - userId: {}, 조회된 채널 수: {}", userId, channels.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }
}
