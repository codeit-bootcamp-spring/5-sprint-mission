package com.sprint.mission.discodeit.controller;

import static com.sprint.mission.discodeit.service.basic.BasicChannelService.*;
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

import com.sprint.mission.discodeit.domain.dto.ChannelDetail;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelResult;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelResult;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelResult;
import com.sprint.mission.discodeit.domain.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.domain.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.domain.request.UpdatePublicChannelRequest;
import com.sprint.mission.discodeit.domain.response.CreatePrivateChannelResponse;
import com.sprint.mission.discodeit.domain.response.CreatePublicChannelResponse;
import com.sprint.mission.discodeit.domain.response.ErrorResponse;
import com.sprint.mission.discodeit.domain.response.ReadChannelResponse;
import com.sprint.mission.discodeit.domain.response.UpdateChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;

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

	@ApiResponses(value = {
	  @ApiResponse(
		responseCode = "201",
		description = "Public Channel이 성공적으로 생성됨",
		content = @Content(schema = @Schema(implementation = CreatePublicChannelResponse.class))
	  )
	})
	@PostMapping("/public")
	public ResponseEntity<CreatePublicChannelResponse> createPublicChannel(
	  @RequestBody @Valid CreatePublicChannelRequest request) {

		CreatePublicChannelResult result = channelService.createPublic(CreatePublicChannelDTO.builder()
		  .name(request.getName())
		  .description(request.getDescription())
		  .build());

		URI location = URI.create("api/channels");
		return ResponseEntity.created(location).body(toCreatePublicChannelResponse(result));

	}

	@ApiResponses(value = {
	  @ApiResponse(
		responseCode = "201",
		description = "Private Channel이 성공적으로 생성됨",
		content = @Content(schema = @Schema(implementation = CreatePrivateChannelResponse.class))
	  )
	})
	@PostMapping("/private")
	public ResponseEntity<CreatePrivateChannelResponse> createPrivateChannel(
	  @RequestBody @Valid CreatePrivateChannelRequest request) {

		CreatePrivateChannelResult result = channelService.createPrivate(CreatePrivateChannelDTO.builder()
		  .UserIds(request.getParticipantIds())
		  .build());

		URI location = URI.create("api/channels");
		return ResponseEntity.created(location).body(toCreatePrivateChannelResponse(result));
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
	@ApiResponses(value = {
	  @ApiResponse(
		responseCode = "200",
		description = "Channel 정보가 성공적으로 수정됨",
		content = @Content(schema = @Schema(implementation = UpdateChannelResponse.class))
	  ),
	  @ApiResponse(
		responseCode = "400",
		description = "Private Channel은 수정할 수 없음",
		content = @Content(
		  schema = @Schema(implementation = ErrorResponse.class),
		  examples = {
			@ExampleObject(
			  value = "{ \"status\": 400, \"errMessage\": \"Private channel cannot be updated\" }"
			)
		  }
		)
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
	public ResponseEntity<UpdateChannelResponse> updatePublicChannel(
	  @Parameter(
		description = "수정할 Channel ID"
	  )
	  @PathVariable UUID channelId,
	  @RequestBody @Valid UpdatePublicChannelRequest request) {
		UpdateChannelResult result = channelService.update(UpdateChannelDTO.builder()
		  .id(channelId)
		  .name(request.getNewName())
		  .description(request.getNewDescription())
		  .build());
		return ResponseEntity.status(OK).body(toUpdateChannelResponse(result));
	}

	@ApiResponses(value = {
	  @ApiResponse(
		responseCode = "200",
		description = "Channel 목록 조회 성공"
	  )
	})
	@GetMapping
	public ResponseEntity<List<ReadChannelResponse>> getAllByUserId(
	  @Parameter(description = "조회할 User ID")
	  @RequestParam UUID userId) {
		List<ChannelDetail> channelDetails = channelService.readAllByUserId(userId);

		List<ReadChannelResponse> body = channelDetails.stream().map(
			BasicChannelService::channelDetailsToReadChannelResponse)
		  .toList();

		return ResponseEntity.ok(body);
	}

}
