package com.sprint.mission.discodeit.controller.channel;

import com.sprint.mission.discodeit.dto.request.chnanel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.service.channel.ChannelService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping("/public")
  @ResponseStatus(HttpStatus.CREATED)
  public ChannelResponse createPublic(@Valid @RequestBody PublicChannelCreateRequest req) {
    return channelService.create(req);
  }

  @PostMapping("/private")
  @ResponseStatus(HttpStatus.CREATED)
  public ChannelResponse createPrivate(@Valid @RequestBody PrivateChannelCreateRequest req) {
    return channelService.create(req);
  }

  @DeleteMapping("/{channelId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
  }

  @PatchMapping("/{channelId}")
  @ResponseStatus(HttpStatus.OK)
  public ChannelResponse update(@PathVariable("channelId") UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest req) {
    return channelService.update(channelId, req);
  }
}
