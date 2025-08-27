package com.sprint.mission.discodeit.presentation.api;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
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

@Tag(name = "Message", description = "메시지 API")
public interface MessageApi {

    @Operation(summary = "메시지 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "메시지 생성 성공",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 채널을 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "사용자 또는 채널을 찾을 수 없습니다."))
            )
    })
    ResponseEntity<MessageResponse> create(
            @Parameter(description = "생성할 메시지 요청 DTO",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            CreateMessageRequest request
    );

    @Operation(summary = "채널별 메시지 목록 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageResponse.class)))
            )
    })
    ResponseEntity<List<MessageResponse>> findAllByChannel(
            @Parameter(description = "채널 ID") UUID channelId
    );

    @Operation(summary = "메시지 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "수정할 수 없거나 존재하지 않는 메시지 (컨트롤러에서 204 반환)"
            )
    })
    ResponseEntity<MessageResponse> update(
            @Parameter(description = "수정할 메시지 요청 DTO",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            UpdateMessageRequest request
    );

    @Operation(summary = "메시지 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "메시지를 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "메시지를 찾을 수 없습니다."))
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "메시지 ID") UUID messageId
    );
}