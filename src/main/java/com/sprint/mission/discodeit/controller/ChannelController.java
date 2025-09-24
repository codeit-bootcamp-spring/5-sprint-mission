package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Slf4j
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping("/public")
  public ResponseEntity<ChannelDto> create(
      @RequestBody @Valid PublicChannelCreateRequest request) {
    log.debug("POST /api/channels/public - request={}", request.toString());

    ChannelDto dto = channelService.create(request);
    log.info("Public channel created: {}", dto.toString());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelDto> create(
      @RequestBody @Valid PrivateChannelCreateRequest request) {
    log.debug("POST /api/channels/private - request={}", request.toString());

    ChannelDto dto = channelService.create(request);
    log.info(
        "Private channel created: id={}, type={}, name={} description={}, participants={}, lastMessageAt={}",
        dto.id(),
        dto.type(),
        dto.name(),
        dto.description(),
        dto.participants().size(),
        dto.lastMessageAt());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @RequestBody @Valid PublicChannelUpdateRequest request) {
    log.debug("PATCH /api/channels/{channelId} - channelId={}, request={}",
        channelId, request.toString());

    ChannelDto dto = channelService.update(channelId, request);
    log.info(
        "Public channel updated: id={}, type={}, name={} description={}, participants={}, lastMessageAt={}",
        dto.id(),
        dto.type(),
        dto.name(),
        dto.description(),
        dto.participants().size(),
        dto.lastMessageAt());

    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    log.info("Public channel deleted: id={}", channelId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUserId(
      @RequestParam UUID userId) {
    List<ChannelDto> channelDtos = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(channelDtos);
  }
}
