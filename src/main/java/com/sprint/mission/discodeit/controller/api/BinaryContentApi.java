package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "BinaryContent API")
public interface BinaryContentApi {

    @Operation(summary = "바이너리 컨텐츠 단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BinaryContentDto.class)))
    })
    ResponseEntity<BinaryContentDto> find(
            @Parameter(description = "조회할 파일 ID")
            UUID binaryContentId
    );

    @Operation(summary = "바이너리 컨텐츠 다건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentDto.class))))
    })
    ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @Parameter(description = "조회할 파일 ID 목록")
            List<UUID> binaryContentIds
    );
}
