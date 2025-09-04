package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage  binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  @GetMapping(path = "{binaryContentId}")
  public ResponseEntity<BinaryContent> find(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }

  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
    // 1. DB에서 메타데이터 조회
    BinaryContent entity = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException(
                    "BinaryContent with id " + binaryContentId + " not found"));

    // 2. Storage에서 InputStream 얻어 Resource 생성
    Resource resource;
    try {
      InputStream inputStream = binaryContentStorage.get(entity.getId());
      resource = new InputStreamResource(inputStream);

      if (!resource.exists()) {
        throw new FileNotFoundException("File not found: " + entity.getFileName());
      }
    } catch (IOException e) {
      // 파일 읽기 실패 시 404 또는 500 처리
      return ResponseEntity.notFound().build();
    }

    // 3. 파일명 인코딩 (한글 파일명 대비)
    String encodedFileName;
    try {
      encodedFileName = UriUtils.encode(entity.getFileName(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      encodedFileName = entity.getFileName();
    }

    // 4. ResponseEntity<Resource> 반환 (다운로드)
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(entity.getContentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(entity.getSize()))
            .body(resource);
  }


  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }
}
