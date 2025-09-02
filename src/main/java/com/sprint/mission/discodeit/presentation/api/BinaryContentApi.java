package com.sprint.mission.discodeit.presentation.api;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
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

@Tag(name = "BinaryContent", description = "파일(바이너리 컨텐츠) API")
public interface BinaryContentApi {

    @Operation(summary = "파일 단건 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BinaryContentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "파일을 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "파일을 찾을 수 없습니다."))
            )
    })
    ResponseEntity<BinaryContentResponse> findOne(
            @Parameter(description = "파일 ID") UUID id
    );

    @Operation(summary = "파일 다건 조회 (ids 쿼리 파라미터)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "파일을 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "파일을 찾을 수 없습니다."))
            )
    })
    ResponseEntity<List<BinaryContentResponse>> findMany(
            @Parameter(description = "파일 ID 목록 (쿼리 파라미터: ids)") List<UUID> ids
    );
}