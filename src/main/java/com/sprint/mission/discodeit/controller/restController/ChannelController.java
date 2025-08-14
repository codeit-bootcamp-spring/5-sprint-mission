package com.sprint.mission.discodeit.controller.restController;

import com.sprint.mission.discodeit.dto.request.AddPrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(path="/public", method = RequestMethod.POST)
    public Channel registerPublicChannel(
            @RequestBody AddPublicChannelRequest addPublicChannelRequest
    ) {
        return channelService.addPublicChannel(addPublicChannelRequest);
    }

    @RequestMapping(path="/private", method = RequestMethod.POST)
    public Channel registerPrivateChannel(
            @RequestBody AddPrivateChannelRequest addPrivateChannelRequest
    ){
        return channelService.addPrivateChannel(addPrivateChannelRequest);
    }

    @RequestMapping(path="/public/update/{channelId}", method = RequestMethod.POST)
    public Channel updatePublicChannel(
            @PathVariable UUID channelId,
            @RequestBody UpdateChannelRequest updateChannelRequest
    ){
        return channelService.updateChannel(updateChannelRequest, channelId);
    }

    @RequestMapping(path="/delete/{channelId}", method = RequestMethod.DELETE)
    public void deleteChannel(
            @PathVariable UUID channelId
    ){
        channelService.deleteChannel(channelId);
    }

    @RequestMapping(path="/{userId}", method = RequestMethod.GET)
    public List<GetChannelByIdResponse> channelsByUserId(
            @PathVariable UUID userId
    ){
        return channelService.getAllChannelByUserId(userId);
    }
}
