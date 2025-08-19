package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.channel.ChannelLeaveRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelCreateResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDeleteResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelLeaveResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
	private final ChannelService channelService;

	@RequestMapping(path = "/public",method = RequestMethod.POST)
	public ResponseEntity<ChannelCreateResponse> createPublicChannel(@RequestBody PublicChannelCreateRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request));
	}

	@RequestMapping(path="/private",method = RequestMethod.POST)
	public ResponseEntity<ChannelCreateResponse> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request));
	}

	@RequestMapping(path = "/{channelId}", method = RequestMethod.PATCH)
	public ResponseEntity<ChannelResponse> updateChannel(
			@PathVariable UUID channelId,
			@RequestBody ChannelUpdateRequest request) {
		ChannelResponse response = channelService.updateChannel(channelId, request);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ChannelResponse>> getChannels(@RequestParam UUID userId) {
		List<ChannelResponse> channels = channelService.findChannelsByUserId(userId);
		return ResponseEntity.ok(channels);
	}

	@RequestMapping(path = "/{channelId}/leave", method = RequestMethod.POST)
	public ResponseEntity<ChannelLeaveResponse> leaveChannel(@PathVariable UUID channelId, @RequestBody ChannelLeaveRequest request) {
		// PathVariable의 channelId와 RequestBody의 channelId 일치성 검증
		if (!channelId.equals(request.getChannelId())) {
			return ResponseEntity.badRequest().build();
		}
		ChannelLeaveResponse response = channelService.leaveChannel(request);
		return ResponseEntity.ok(response);
	}


	@RequestMapping(path = "/{channelId}", method = RequestMethod.DELETE)
	public ResponseEntity<ChannelDeleteResponse> deleteChannel(@PathVariable UUID channelId) {
		ChannelDeleteResponse response = channelService.deleteChannel(channelId);
		return ResponseEntity.ok(response);
	}
}
