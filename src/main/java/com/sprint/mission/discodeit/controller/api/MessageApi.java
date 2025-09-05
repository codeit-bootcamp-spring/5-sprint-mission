package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
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
      @ApiResponse(responseCode = "201",
          description = "Message가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))),
      @ApiResponse(responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음")
  })
  ResponseEntity<MessageDto> create(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "JSON 파트(messageCreateRequest)",
          required = true,
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = MessageCreateRequest.class)))
      MessageCreateRequest messageCreateRequest,

      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "첨부 파일들(attachments)",
          required = false,
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
      List<MultipartFile> attachments
  );

  @Operation(summary = "Message 내용 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "성공",
          content = @Content(schema = @Schema(implementation = MessageDto.class))),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  })
  ResponseEntity<MessageDto> update(UUID messageId, MessageUpdateRequest request);

  @Operation(summary = "Message 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "성공"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  })
  ResponseEntity<Void> delete(UUID messageId);

  @Operation(summary = "Channel의 Message 목록 조회(페이징)")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "성공",
          content = @Content(schema = @Schema(implementation = PageResponse.class)))
  })
  ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(UUID channelId, int page, int size);
}
