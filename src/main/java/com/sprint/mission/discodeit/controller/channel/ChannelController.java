package com.sprint.mission.discodeit.controller.channel;

import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelSaveResponse;
import com.sprint.mission.discodeit.service.channel.ChannelService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/channels", produces = "application/json")
public class ChannelController {

  private final ChannelService channelService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<ChannelResponse> findAll(@RequestParam("userId") UUID userId) {
    return channelService.findAll(userId);
  }

  @PostMapping(path = "/public", consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public ChannelSaveResponse createPublic(@Valid @RequestBody PublicChannelCreateRequest req) {
    return channelService.create(req);
  }

  @PostMapping(path = "/private", consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public ChannelSaveResponse createPrivate(@Valid @RequestBody PrivateChannelCreateRequest req) {
    return channelService.create(req);
  }

  @DeleteMapping(path = "/{channelId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
  }

  @PatchMapping(path = "/{channelId}", consumes = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public ChannelSaveResponse update(@PathVariable("channelId") UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest req) {
    return channelService.update(channelId, req);
  }
}
