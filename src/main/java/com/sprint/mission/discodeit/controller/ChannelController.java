package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Public Channel 생성")
  @PostMapping("/public")
  public ResponseEntity<Channel> create(
      @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
    Channel publicChannel = channelService.create(publicChannelCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
  }

  @Operation(summary = "Private Channel 생성")
  @PostMapping("/private")
  public ResponseEntity<Channel> create(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
    Channel privateChannel = channelService.create(privateChannelCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
  }

  @Operation(summary = "Channel 정보 수정")
  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {
    Channel updatedChannel = channelService.update(channelId, publicChannelUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }

  @Operation(summary = "Channel 삭제")
  @DeleteMapping("/{channelId}")
  public ResponseEntity<String> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Channel이 성공적으로 삭제됨");
  }

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @GetMapping
  public ResponseEntity<List<ChannelFindResponse>> findAllByUserId(
      @RequestParam(value = "userId") UUID userId) {
    List<ChannelFindResponse> channelFindResponseList = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(channelFindResponseList);
  }


}
