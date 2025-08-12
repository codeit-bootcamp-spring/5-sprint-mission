package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;


    @RequestMapping(method = RequestMethod.POST)
    public BinaryContent addBinaryContent(
            @RequestBody byte[] binaryContent
    ) {
         return binaryContentService.addBinaryContent(binaryContent);
    }

    @RequestMapping(path="find/{contentId}" ,method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> getBinaryContentById(
            @PathVariable UUID contentId
    ){
        return ResponseEntity.ok(binaryContentService.getBinaryContentById(contentId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> getBinaryContent(){
        return binaryContentService.getAllBinaryContent();
    }
}
