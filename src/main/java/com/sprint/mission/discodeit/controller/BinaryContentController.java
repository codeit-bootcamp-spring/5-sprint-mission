package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    //[ ] 바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
    @RequestMapping(path = "find")
    public ResponseEntity<BinaryContent> find(
            @RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    @RequestMapping(path = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAll(
            @RequestPart List<UUID> binaryContentIds
    ) {
        List<BinaryContent> binaryContentList = binaryContentService.findAllByIdIn(
                binaryContentIds);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContentList);
    }


}