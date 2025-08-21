package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> findById(@RequestParam("binaryContentId") UUID id) {
        BinaryContent binaryContent = binaryContentService.find(id);

        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }

    @RequestMapping(value = "/findAllByIdIn", method = RequestMethod.GET)
    public ResponseEntity<ApiResult<List<BinaryContent>>> findAllByIdIn(@RequestParam("id") List<UUID> id) {
        List<BinaryContent> binaryContent = binaryContentService.findAllByIdIn(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.ok(binaryContent));
    }
}
