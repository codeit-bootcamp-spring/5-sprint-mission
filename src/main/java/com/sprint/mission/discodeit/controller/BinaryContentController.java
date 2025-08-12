package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binary-content")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;


    @RequestMapping(method = RequestMethod.POST)
    public BinaryContent addBinaryContent(
            @RequestBody byte[] binaryContent
    ) {
         return binaryContentService.addBinaryContent(binaryContent);
    }

    @RequestMapping(path="/{contentId}" ,method = RequestMethod.GET)
    public BinaryContent getBinaryContentById(
            @PathVariable UUID contentId
    ){
        return binaryContentService.getBinaryContentById(contentId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> getBinaryContent(){
        return binaryContentService.getAllBinaryContent();
    }
}
