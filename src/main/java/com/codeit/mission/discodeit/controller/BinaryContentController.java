package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binary")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> find(@RequestParam List<UUID> binaryContentIds){
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds).stream().toList();
        return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
    }
}
