package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

  private final BasicBinaryContentService binaryContentService;

  @RequestMapping(path = "/find",
      method = RequestMethod.GET)
  public ResponseEntity<byte[]> find(@RequestParam("binaryContentId") UUID binaryContentId) {
    BinaryContent file = binaryContentService.findById(binaryContentId);

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.parseMediaType(file.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + file.getFileName() + "\"")
        .body(file.getBytes());
  }
}
