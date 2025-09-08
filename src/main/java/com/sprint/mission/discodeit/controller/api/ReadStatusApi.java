package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
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

@Tag(name = "ReadStatus", description = "ReadStatus API")
public interface ReadStatusApi {

    @Operation(summary = "읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = ReadStatusDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자/채널 없음",
                    content = @Content(examples = @ExampleObject(value = "User {id} not found")))
    })
    ResponseEntity<ReadStatusDto> create(
            @Parameter(description = "읽음 상태 생성 정보")
            ReadStatusCreateRequest request
    );

    @Operation(summary = "읽음 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ReadStatusDto.class))),
            @ApiResponse(responseCode = "404", description = "읽음 상태 없음",
                    content = @Content(examples = @ExampleObject(value = "ReadStatus {readStatusId} not found")))
    })
    ResponseEntity<ReadStatusDto> update(
            @Parameter(description = "수정할 읽음 상태 ID")
            UUID readStatusId,
            @Parameter(description = "수정 정보")
            ReadStatusUpdateRequest request
    );

    @Operation(summary = "사용자별 읽음 상태 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusDto.class))))
    })
    ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @Parameter(description = "대상 사용자 ID")
            UUID userId
    );
}
