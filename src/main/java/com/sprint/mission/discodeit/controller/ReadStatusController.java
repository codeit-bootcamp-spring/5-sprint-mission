package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readstatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // [POST] 특정 채널의 메시지 수신정보 생성
    @RequestMapping(path = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest req) {
        ReadStatus created = readStatusService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // [PUT] 특정 채널의 메시지 수신정보 수정
    @RequestMapping(path = "update/{readStatusId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadStatus> update(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest req
    ) {
        ReadStatus updated = readStatusService.update(readStatusId, req);
        return ResponseEntity.ok(updated);
    }

    // [GET] 특정 사용자의 메시지 수신정보 조회
    @RequestMapping(path = "user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findByUser(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
