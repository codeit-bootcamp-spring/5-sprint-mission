package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부파일 API")
public class BinaryContentController {

  private final BasicBinaryContentService binaryContentService;

  @Operation(summary = "첨부 파일 조회")
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<byte[]> find(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContent file = binaryContentService.findById(binaryContentId);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.parseMediaType(file.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + file.getFileName() + "\"")
        .body(file.getBytes());
  }

  @Operation(summary = "여러 첨부 파일 조회")
  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    List<BinaryContent> files = binaryContentService.findAllByIdIn(binaryContentIds);

    return ResponseEntity.status(HttpStatus.OK).body(files);
  }
}
