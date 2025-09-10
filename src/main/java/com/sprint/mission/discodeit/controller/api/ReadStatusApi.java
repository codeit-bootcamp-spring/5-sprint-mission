package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

  @Operation(summary = "사용자의 Message 읽음 상태 전체 조회")
  @ApiResponse(
      responseCode = "200",
      description = "조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusDto.class)))
  )
  ResponseEntity<List<ReadStatusDto>> findByUser(
      @Parameter(description = "조회할 User ID") UUID userId
  );

  @Operation(summary = "채널 기준 읽음 시각 갱신(없으면 생성, 단조 증가)")
  @ApiResponse(
      responseCode = "200",
      description = "갱신 성공",
      content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
  )
  ResponseEntity<ReadStatusDto> markRead(
      @Parameter(description = "읽음 표시할 Channel ID(경로 변수)") UUID channelId,
      @Parameter(description = "대상 User ID(쿼리 파라미터)") UUID userId,
      @Parameter(
          description = "갱신할 읽음 시각(없으면 서버 now 사용)",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ReadStatusUpdateRequest.class))
      ) ReadStatusUpdateRequest request
  );
}
