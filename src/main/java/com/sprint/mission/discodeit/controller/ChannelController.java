package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping("/public")
  public ResponseEntity<ChannelDto> create(
      @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
    ChannelDto publicChannel = channelService.create(publicChannelCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelDto> create(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
    ChannelDto privateChannel = channelService.create(privateChannelCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {
    ChannelDto updatedChannel = channelService.update(channelId, publicChannelUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUserId(
      @RequestParam UUID userId) {
    List<ChannelDto> channelDtos = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(channelDtos);
  }
}
