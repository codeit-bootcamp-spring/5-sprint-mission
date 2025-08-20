package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    public BinaryContentController(@Qualifier("basicBinaryContentService") BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> getBinaryContents(@RequestParam("ids") List<UUID> ids) {
        List<BinaryContentResponse> response = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BinaryContentResponse> createBinaryContent(@RequestBody BinaryContentCreateRequest request) {
        BinaryContentResponse response = binaryContentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
