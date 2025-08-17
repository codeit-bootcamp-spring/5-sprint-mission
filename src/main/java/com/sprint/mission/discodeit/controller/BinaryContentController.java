package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    @Qualifier("basicBinaryContentService")
    private final BinaryContentService binaryContentService;

    // 바이너리 파일 1개 또는 여러 개 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> getBinaryContents(@RequestParam("ids") List<UUID> ids) {
        List<BinaryContentResponse> response = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(response);
    }
}
