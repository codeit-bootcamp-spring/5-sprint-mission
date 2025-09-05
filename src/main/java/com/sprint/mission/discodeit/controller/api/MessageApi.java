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

  @Operation(summary = "Message 생성")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Message가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found"))
      ),
  })
  ResponseEntity<MessageDto> create(
      @Parameter(
          description = "Message 생성 정보 (JSON 파트: messageCreateRequest)",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = MessageCreateRequest.class)
          )
      )
      MessageCreateRequest messageCreateRequest,
      @Parameter(
          description = "첨부 파일들 (파트 이름: attachments)",
          content = @Content(
              mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
          )
      )
      List<MultipartFile> attachments
  );

  @Operation(summary = "Message 내용 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Message가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
      ),
  })
  ResponseEntity<MessageDto> update(
      @Parameter(description = "수정할 Message ID") UUID messageId,
      @Parameter(
          description = "수정할 Message 내용",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = MessageUpdateRequest.class))
      )
      MessageUpdateRequest request
  );

  @Operation(summary = "Message 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
      ),
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Message ID") UUID messageId
  );

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Message 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageDto.class)))
      )
  })
  ResponseEntity<List<MessageDto>> findAllByChannelId(
      @Parameter(description = "조회할 Channel ID") UUID channelId
  );
}
