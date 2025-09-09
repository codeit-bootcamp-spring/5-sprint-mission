package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Operation(summary = "BinaryContent 조회")
  @GetMapping("/{id}")
  public ResponseEntity<BinaryContentDto.DetailResponse> getBinaryContent(@PathVariable UUID id) {
    return ResponseEntity.ok(
        binaryContentMapper.toDetailResponse(
            binaryContentService.find(id)
        )
    );
  }

  @Operation(summary = "BinaryContent 다운로드")
  @GetMapping("/{id}/download")
  public ResponseEntity<?> downloadBinaryContent(@PathVariable UUID id) {
    return binaryContentStorage.download(id);
  }
}
