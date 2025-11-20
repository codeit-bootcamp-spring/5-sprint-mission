package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.docs.ChannelControllerDocs;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController implements ChannelControllerDocs {

    private final ChannelService channelService;

    @PostMapping("/public")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPublic(
        @RequestBody
        @Valid
        PublicChannelCreateRequest request
    ) {
        return channelService.create(request);
    }

    @PostMapping("/private")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPrivate(
        @RequestBody
        @Valid
        PrivateChannelCreateRequest request
    ) {
        return channelService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ChannelDto> findAll(
        @RequestParam
        UUID userId
    ) {
        return channelService.findAll(userId);
    }

    @PatchMapping("/{channelId}")
    @ResponseStatus(HttpStatus.OK)
    public ChannelDto update(
        @PathVariable
        UUID channelId,

        @RequestBody
        @Valid
        PublicChannelUpdateRequest request
    ) {
        return channelService.update(
            channelId,
            request
        );
    }

    @DeleteMapping("/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }
}
