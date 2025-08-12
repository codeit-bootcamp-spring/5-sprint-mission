package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    @ResponseBody
    @RequestMapping(value = {"/createPublic", "/create"}, method = RequestMethod.POST)
    public String createPublicChannel(@RequestBody ChannelCreateRequest request) {
        Channel publicChannel = channelService.createPublic(request);
        return "public 채널 생성 성공\n" + publicChannel.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/createPrivate", method = RequestMethod.POST)
    public String createPrivateChannel(@RequestBody ChannelCreateRequest request) {
        Channel privateChannel = channelService.createPrivate(request);
        return "private 채널 생성 성공\n" + privateChannel.toString();
    }


    @ResponseBody
    @RequestMapping(value = {"/updatePublic", "/update"}, method = RequestMethod.POST)
    public String updatePublicChannel(@RequestBody ChannelUpdateRequest request) {
        Channel updatedChannel = channelService.update(request);
        return "업데이트 성공\n" + updatedChannel.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String deleteChannel(@PathVariable UUID id) {
        ChannelFindResponse channelFindResponse = channelService.findById(id);
        channelService.delete(id);
        return "삭제 성공\n" + channelFindResponse.toString();
    }

    @ResponseBody
    @RequestMapping(value = {"/listForUser/{id}", "/listForUser", "/listPublic"}, method = RequestMethod.GET)
    public String findAllByUserId(@PathVariable(value = "id", required = false) UUID id) {
        List<ChannelFindResponse> channelFindResponseList = channelService.findAllByUserId(id);
        return channelFindResponseList.toString().replace("], ", "]\n");
    }


}
