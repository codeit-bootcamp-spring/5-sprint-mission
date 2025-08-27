package com.sprint.mission.discodeit.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부파일 API")
public interface BinaryContentApi {

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
      @ApiResponse(responseCode = "404", description = "첨부 파일 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")))
  })
  @GetMapping(path = "{binaryContentId}")
  @Operation(summary = "첨부 파일 조회", description = "조회할 첨부 파일 ID")
  ResponseEntity<BinaryContent> find(@PathVariable("binaryContentId") UUID binaryContentId);

  @Operation(summary = "여러 첨부 파일 조회", description = "조회할 첨부 파일 ID 목록")
  @GetMapping
  ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds);

}
