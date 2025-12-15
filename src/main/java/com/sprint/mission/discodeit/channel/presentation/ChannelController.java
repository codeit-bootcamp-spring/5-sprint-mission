package com.sprint.mission.discodeit.channel.presentation;

import com.sprint.mission.discodeit.channel.application.ChannelService;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.presentation.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelControllerDocs {

    private final ChannelService channelService;

    @PostMapping("/public")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPublic(@RequestBody @Valid PublicChannelCreateRequest request) {
        return channelService.create(request);
    }

    @PostMapping("/private")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPrivate(@RequestBody @Valid PrivateChannelCreateRequest request) {
        return channelService.create(request);
    }

    @GetMapping
    public List<ChannelDto> findAll(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        return channelService.findAll(userDetails.getUserDetailsDto().id());
    }

    @PatchMapping("/{channelId}")
    public ChannelDto update(
        @PathVariable UUID channelId,
        @RequestBody @Valid PublicChannelUpdateRequest request
    ) {
        return channelService.update(channelId, request);
    }

    @DeleteMapping("/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID channelId) {
        channelService.deleteById(channelId);
    }
}
