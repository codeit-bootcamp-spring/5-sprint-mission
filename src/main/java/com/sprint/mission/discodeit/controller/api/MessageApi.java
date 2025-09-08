package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

    @Operation(summary = "메시지 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메시지 생성 성공",
                    content = @Content(schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "404", description = "채널/사용자 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel {id} not found")))
    })
    ResponseEntity<MessageDto> create(
            @Parameter(description = "메시지 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            MessageCreateRequest messageCreateRequest,
            @Parameter(description = "첨부 파일들",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            List<MultipartFile> attachments
    );

    @Operation(summary = "메시지 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 수정 성공",
                    content = @Content(schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "404", description = "메시지 없음",
                    content = @Content(examples = @ExampleObject(value = "Message {messageId} not found")))
    })
    ResponseEntity<MessageDto> update(
            @Parameter(description = "수정할 메시지 ID")
            UUID messageId,
            @Parameter(description = "수정 내용")
            MessageUpdateRequest request
    );

    @Operation(summary = "메시지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "메시지 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "메시지 없음",
                    content = @Content(examples = @ExampleObject(value = "Message {messageId} not found")))
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 메시지 ID")
            UUID messageId
    );

    @Operation(summary = "채널의 모든 메시지 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageDto.class))))
    })
    ResponseEntity<List<MessageDto>> findAllByChannelId(
            @Parameter(description = "조회할 채널 ID")
            UUID channelId
    );
}
