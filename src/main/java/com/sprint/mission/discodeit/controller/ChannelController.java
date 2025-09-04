package com.sprint.mission.discodeit.controller;

import static org.springframework.http.HttpStatus.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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

import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.domain.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.domain.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.domain.request.UpdatePublicChannelRequest;
import com.sprint.mission.discodeit.domain.response.ErrorResponse;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

	private final ChannelService channelService;
	private final ChannelMapper channelMapper;

	@PostMapping("/public")
	public ResponseEntity<ChannelResponse> createPublicChannel(
	  @RequestBody @Valid CreatePublicChannelRequest request) {

		ChannelDto result = channelService.createPublic(CreatePublicChannelDTO.builder()
		  .name(request.getName())
		  .description(request.getDescription())
		  .build());

		URI location = URI.create("api/channels");
		return ResponseEntity.created(location).body(channelMapper.toResponse(result));

	}

	@PostMapping("/private")
	public ResponseEntity<ChannelResponse> createPrivateChannel(
	  @RequestBody @Valid CreatePrivateChannelRequest request) {

		ChannelDto result = channelService.createPrivate(CreatePrivateChannelDTO.builder()
		  .UserIds(request.getParticipantIds())
		  .build());

		URI location = URI.create("api/channels");
		return ResponseEntity.created(location).body(channelMapper.toResponse(result));
	}

	@ApiResponses(value = {
	  @ApiResponse(
		responseCode = "204",
		description = "Channel이 성공적으로 삭제됨"
	  ),
	  @ApiResponse(
		responseCode = "404",
		description = "Channel을 찾을 수 없음",
		content = @Content(
		  schema = @Schema(implementation = ErrorResponse.class),
		  examples = {
			@ExampleObject(
			  value = "{ \"status\": 404, \"errMessage\": \"Channel with id {channelId} not found\" }"
			)
		  }
		)
	  )
	})
	@DeleteMapping("{channelId}")
	public ResponseEntity<Void> deleteChannel(
	  @Parameter(
		description = "삭제할 Channel ID (UUID 형식)",
		required = true
	  )
	  @PathVariable UUID channelId) {
		channelService.delete(channelId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{channelId}")
	public ResponseEntity<ChannelResponse> updatePublicChannel(
	  @Parameter(
		description = "수정할 Channel ID"
	  )
	  @PathVariable UUID channelId,
	  @RequestBody @Valid UpdatePublicChannelRequest request) {
		ChannelDto result = channelService.update(UpdateChannelDTO.builder()
		  .id(channelId)
		  .name(request.getNewName())
		  .description(request.getNewDescription())
		  .build());
		return ResponseEntity.status(OK).body(channelMapper.toResponse(result));
	}

	@GetMapping
	public ResponseEntity<List<ChannelResponse>> getAllByUserId(
	  @Parameter(description = "조회할 User ID")
	  @RequestParam UUID userId) {
		List<ChannelDto> channels = channelService.readAllByUserId(userId);

		List<ChannelResponse> body = channels.stream().map(channelMapper::toResponse).toList();

		return ResponseEntity.ok(body);
	}

}
