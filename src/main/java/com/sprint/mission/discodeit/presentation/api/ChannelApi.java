package com.sprint.mission.discodeit.presentation.api;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 API")
public interface ChannelApi {

    @Operation(summary = "채널 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "채널 생성 성공",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))
            )
    })
    ResponseEntity<ChannelResponse> create(
            @Parameter(description = "생성할 채널 요청 DTO",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            CreateChannelRequest request
    );

    @Operation(summary = "채널 단건 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "채널을 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "Channel with id {id} not found"))
            )
    })
    ResponseEntity<ChannelResponse> find(
            @Parameter(description = "채널 ID") UUID id
    );

    @Operation(summary = "특정 사용자에게 보이는 채널 목록 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelResponse.class)))
            )
    })
    ResponseEntity<List<ChannelResponse>> findVisible(
            @Parameter(description = "사용자 ID") UUID userId
    );

    @Operation(summary = "채널 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "수정할 수 없거나 존재하지 않는 채널",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "수정할 수 없거나 존재하지 않는 채널입니다"))
            )
    })
    ResponseEntity<ChannelResponse> update(
            @Parameter(description = "수정할 채널 요청 DTO",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            UpdateChannelRequest request
    );

    @Operation(summary = "채널 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "채널을 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "Channel with id {id} not found"))
            )
    })
    ResponseEntity<ChannelResponse> delete(
            @Parameter(description = "채널 ID") UUID id
    );
}