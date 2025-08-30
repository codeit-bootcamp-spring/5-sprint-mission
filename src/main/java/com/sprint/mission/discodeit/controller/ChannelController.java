package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
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
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelRepository channelRepository;
    private final ChannelService channelService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ChannelDto> findAll(@RequestParam UUID userId) {
        return channelService.findAll(userId);
    }

    @PostMapping(path = "/public")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPublic(

        @RequestBody
        @Valid
        PublicChannelCreateRequest req
    ) {

        return channelService.create(req);
    }

    @PostMapping(path = "/private")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPrivate(@RequestBody @Valid PrivateChannelCreateRequest req) {
        return channelService.create(req);
    }

    @DeleteMapping(path = "/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }

    @PatchMapping(path = "/{channelId}")
    @ResponseStatus(HttpStatus.OK)
    public ChannelDto update(
        @PathVariable UUID channelId,
        @RequestBody @Valid PublicChannelUpdateRequest req
    ) {
        return channelService.update(channelId, req);
    }
}
