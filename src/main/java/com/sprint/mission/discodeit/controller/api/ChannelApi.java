package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "공개 채널 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "채널 생성 성공",
                    content = @Content(schema = @Schema(implementation = ChannelDto.class))),
            @ApiResponse(responseCode = "400", description = "유효성 오류",
                    content = @Content(examples = @ExampleObject(value = "Channel name already exists")))
    })
    ResponseEntity<ChannelDto> create(
            @Parameter(description = "공개 채널 생성 정보")
            PublicChannelCreateRequest request
    );

    @Operation(summary = "비공개 채널 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "채널 생성 성공",
                    content = @Content(schema = @Schema(implementation = ChannelDto.class)))
    })
    ResponseEntity<ChannelDto> create(
            @Parameter(description = "비공개 채널 생성 정보")
            PrivateChannelCreateRequest request
    );

    @Operation(summary = "채널 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널 수정 성공",
                    content = @Content(schema = @Schema(implementation = ChannelDto.class))),
            @ApiResponse(responseCode = "404", description = "채널 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel {channelId} not found")))
    })
    ResponseEntity<ChannelDto> update(
            @Parameter(description = "수정할 채널 ID")
            UUID channelId,
            @Parameter(description = "수정 정보")
            PublicChannelUpdateRequest request
    );

    @Operation(summary = "채널 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "채널 삭제 성공")
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 채널 ID")
            UUID channelId
    );

    @Operation(summary = "사용자 채널 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))))
    })
    ResponseEntity<List<ChannelDto>> findAll(
            @Parameter(description = "대상 사용자 ID")
            UUID userId
    );
}
