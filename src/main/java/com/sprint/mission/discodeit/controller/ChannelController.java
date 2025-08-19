package com.sprint.mission.discodeit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.request.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.DeleteChannelRequest;
import com.sprint.mission.discodeit.dto.request.channel.GetChannelsByUserRequest;
import com.sprint.mission.discodeit.dto.request.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.CreateChannelResponse;
import com.sprint.mission.discodeit.dto.response.channel.DeleteChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
	private final ChannelService channelService;

	@RequestMapping(path = "/public",method = RequestMethod.POST)
	public ResponseEntity<CreateChannelResponse> createPublicChannel(@RequestBody CreatePublicChannelRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request));
	}

	@RequestMapping(path="/private",method = RequestMethod.POST)
	public ResponseEntity<CreateChannelResponse> createPrivateChannel(@RequestBody CreatePrivateChannelRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request));
	}

	@RequestMapping(path= "/update", method = RequestMethod.PATCH)
	public ResponseEntity<ChannelResponse> updateChannel(@RequestBody UpdateChannelRequest request) {
		ChannelResponse response = channelService.updateChannel(request);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ChannelResponse>> getChannels(@ModelAttribute GetChannelsByUserRequest request) {
		List<ChannelResponse> channels = channelService.getChannelsByUserId(request);
		return ResponseEntity.ok(channels);
	}


	@RequestMapping(path="/delete", method = RequestMethod.DELETE)
	public ResponseEntity<DeleteChannelResponse> deleteChannel(@RequestBody DeleteChannelRequest request) {
		DeleteChannelResponse response = channelService.deleteChannel(request);
		return ResponseEntity.ok(response);
	}
}
