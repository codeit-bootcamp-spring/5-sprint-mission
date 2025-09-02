package com.sprint.mission.discodeit.presentation.controller;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.presentation.api.BinaryContentApi;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> findOne(@PathVariable("id") UUID id) {
        return binaryContentService.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("파일을 찾을 수 없습니다."));
    }

    @RequestMapping(method = RequestMethod.GET, params = "ids")
    public ResponseEntity<List<BinaryContentResponse>> findMany(@RequestParam("ids") List<UUID> ids) {
        List<BinaryContentResponse> contentList = binaryContentService.getAllByIdIn(ids);
        if (contentList.isEmpty()) throw new NotFoundException("파일을 찾을 수 없습니다.");
        return ResponseEntity.ok(contentList);
    }
}
