package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;


// 바이너리 파일 다운로드
// * [ ] 바이너리 파일을 1개 또는 여러 개 조회할 수 있다.

// 정적 리소스 서빙
// [ ]  사용자 목록 조회, BinaryContent 파일 조회 API를 다음의 조건을 만족하도록 수정하세요.

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(path = "find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    @RequestMapping(path = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAll(@RequestParam("binaryContentId")List<UUID> request){
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(request);
        return ResponseEntity.ok(binaryContents);
    }
}