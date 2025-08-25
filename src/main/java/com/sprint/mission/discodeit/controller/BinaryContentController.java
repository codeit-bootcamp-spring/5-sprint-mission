package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BinaryContent", description = "첨부파일 API")
@RestController
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  /* MultipartFile : 클라이언트가 업로드한 파일을 컨트롤러에서 받는 용도
   * BinaryContent : 서버에서 파일 저장하고 관리하는 용도 (엔티티)

   * 프론트에서 파일을 올리면, 서버에서 MultipartFile로 받고
   * BinaryContent로 변환해서 저장하는 흐름이다.
   * */


  //파일 등록
  @Operation(summary = "파일 등록")
  @PostMapping("/api/binaryContents")
  public ResponseEntity<UUID> create(@RequestBody BinaryContentCreateRequest request) {
    UUID id = binaryContentService.create(request);
    return ResponseEntity.ok(id);
  }


  //파일 1개 조회
  @Operation(summary = "파일 1개 조회")
  @GetMapping("/api/binaryContents/{binaryContentId}")
  public ResponseEntity<BinaryContent> findById(@PathVariable UUID binaryContentId) {
    BinaryContent file = binaryContentService.findById(binaryContentId);
    return ResponseEntity.ok(file);
  }

  //바이너리 파일 다운로드
  @Operation(summary = "파일 다운로드")
  @RequestMapping(value = "/download", method = RequestMethod.GET)
  public ResponseEntity<byte[]> downloadFile(@RequestParam UUID binaryContentId) {
    BinaryContent file = binaryContentService.findById(binaryContentId);

    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"")
        .header("Content-Type", file.getContentType())
        .body(file.getBytes());
  }


  //파일 여러개 조회
  @Operation(summary = "파일 여러개 조회")
  @GetMapping("/api/binaryContents")
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    List<BinaryContent> files = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok(files);
  }


  //파일 삭제
  @Operation(summary = "파일 삭제")
  @DeleteMapping("/api/binaryContents/{binaryContentId}")
  public ResponseEntity<Void> deleteById(@PathVariable UUID binaryContentId) {
    binaryContentService.deleteById(binaryContentId);
    return ResponseEntity.noContent().build();
  }
}
