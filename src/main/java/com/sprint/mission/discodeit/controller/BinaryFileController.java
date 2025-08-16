package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.sub.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class BinaryFileController {

    private final BinaryContentService binaryContentService;


/*
    @RequestMapping(value = "/{binaryContentId}", method = GET)
    public ResponseEntity<BinaryContent> getBinaryContent(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }
*/

    @RequestMapping(value = "/find", method = GET)
    public ResponseEntity<BinaryContent> getBinaryContentByParam(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContent content = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(content); // { id, filename, contentType, bytes(Base64) } 형태 JSON
    }

    @RequestMapping(value = "/find", method = POST)
    public ResponseEntity<List<BinaryContent>> getBinaryContents(
            @RequestBody List<UUID> ids
    ) {
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(contents);
    }
}
