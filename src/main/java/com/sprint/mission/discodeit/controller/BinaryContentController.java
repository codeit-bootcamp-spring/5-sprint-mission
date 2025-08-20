package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/binaryContent/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto.DetailResponse> getBinaryContent(@PathVariable UUID id) {
        return ResponseEntity.ok(binaryContentService.find(id));
    }
}
