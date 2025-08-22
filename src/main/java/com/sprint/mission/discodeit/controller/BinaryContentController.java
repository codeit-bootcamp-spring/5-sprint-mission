package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    //1개 파일 조회
    @GetMapping(value = "/{id}")
    public ResponseEntity<ApiResult<BinaryContentDto>> findById(@PathVariable("id") UUID id) {
        BinaryContentDto binaryContent = binaryContentService.find(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(binaryContent));
    }

    //여러 파일 조회
    @GetMapping
    public ResponseEntity<ApiResult<List<BinaryContentDto>>> findAllByIdIn(@RequestParam("ids") List<UUID> ids) {
        List<BinaryContentDto> binaryContent = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(binaryContent));
    }
}
