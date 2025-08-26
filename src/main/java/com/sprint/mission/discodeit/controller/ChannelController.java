package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @Operation(summary = "Public Channel 생성")
    @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ChannelDto.class))
            )
    })
    public ResponseEntity<ChannelDto> publicChannelCreate(@RequestBody PublicChannelCreateRequest createRequest) {
        ChannelDto channel = channelService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }


    @Operation(summary = "Private Channel 생성")
    @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ChannelDto.class))
            )
    })
    public ResponseEntity<ChannelDto> privateChannelCreate(@RequestBody PrivateChannelCreateRequest createRequest) {
        ChannelDto channel = channelService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }


    @Operation(summary = "Channel 정보 수정")
    @PatchMapping(value = "/{channelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = ChannelDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Private Channel은 수정할 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
            )
    })
    public ResponseEntity<ChannelDto> channelUpdate(@PathVariable("channelId") UUID channelId,
                                                    @RequestBody PublicChannelUpdateRequest updateRequest) {
        ChannelDto channel = channelService.update(channelId, updateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @Operation(summary = "Channel 삭제")
    @DeleteMapping(value = "/{channelId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "Channel이 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
            )
    })
    public ResponseEntity<Void> channelDelete(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "User가 참여중인 Channel 목록 조회")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Channel 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
            )
    })
    public ResponseEntity<List<ChannelDto>> channelFindAllByUserId(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}
