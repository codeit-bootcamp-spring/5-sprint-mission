package com.sprint.mission.discodeit.controller;
import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID; // UUID import가 누락되어 추가함
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    log.debug("[FILE][GET] id={}", binaryContentId);
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    log.debug("[FILE][GET][DONE] id={}", binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    log.debug("[FILE][LIST] count={}", binaryContentIds.size());
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    log.debug("[FILE][LIST][DONE] returned={}", binaryContents.size());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }

  // 💡 수정된 부분: download 메서드가 클래스 내부로 이동
  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<?> download(
      @PathVariable("binaryContentId") UUID binaryContentId) {
    log.info("[FILE][DOWNLOAD] id={}", binaryContentId);
    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
    log.debug("[FILE][DOWNLOAD][DONE] id={}", binaryContentId);
    return binaryContentStorage.download(binaryContentDto);
  }
} // 💡 닫는 중괄호가 가장 마지막에 위치