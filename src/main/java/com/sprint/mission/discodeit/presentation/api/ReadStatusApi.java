package com.sprint.mission.discodeit.presentation.api;

import com.sprint.mission.discodeit.dto.status.read.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.status.read.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.status.read.UpdateReadStatusRequest;
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

@Tag(name = "ReadStatus", description = "메시지 읽음 상태 API")
public interface ReadStatusApi {

    @Operation(summary = "읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "읽음 상태 생성 성공",
                    content = @Content(schema = @Schema(implementation = ReadStatusResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(examples = @ExampleObject(value = "Invalid request body"))
            )
    })
    ResponseEntity<ReadStatusResponse> create(
            @Parameter(description = "생성할 읽음 상태 요청 DTO")
            CreateReadStatusRequest request
    );

    @Operation(summary = "특정 User의 읽음 상태 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404", description = "해당 User의 읽음 상태 없음",
                    content = @Content(examples = @ExampleObject(value = "ReadStatus for userId {userId} not found"))
            )
    })
    ResponseEntity<List<ReadStatusResponse>> getReadStatus(
            @Parameter(description = "조회할 User ID") UUID userId
    );

    @Operation(summary = "읽음 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "읽음 상태 수정 성공",
                    content = @Content(schema = @Schema(implementation = ReadStatusResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "해당 ReadStatus를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "ReadStatus not found"))
            )
    })
    ResponseEntity<ReadStatusResponse> update(
            @Parameter(description = "수정할 읽음 상태 요청 DTO")
            UpdateReadStatusRequest request
    );
}
