package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping(path = "{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(
          @PathVariable("binaryContentId") UUID binaryContentId) {
    log.info("파일 조회 요청 수신: binaryContentId={}", binaryContentId);

    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);

    log.info("파일 조회 완료: binaryContentId={}, fileName={}",
            binaryContent.id(), binaryContent.fileName());
    return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
          @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    log.info("여러 파일 조회 요청 수신: binaryContentIds.size={}", binaryContentIds.size());

    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);

    log.info("여러 파일 조회 완료: 요청 {}개, 반환 {}개",
            binaryContentIds.size(), binaryContents.size());
    return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
  }

  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<Resource> download(
          @PathVariable("binaryContentId") UUID binaryContentId) {
    log.info("파일 다운로드 요청 수신: binaryContentId={}", binaryContentId);

    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

    log.info("파일 다운로드 시작: binaryContentId={}, fileName={}",
            binaryContentDto.id(), binaryContentDto.fileName());

    ResponseEntity<Resource> response = binaryContentStorage.download(binaryContentDto);

    log.info("파일 다운로드 완료: binaryContentId={}, fileName={}",
            binaryContentDto.id(), binaryContentDto.fileName());
    return response;
  }
}