package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.codeit.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request가 필요합니다.");
        }
        if (request.userId() == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }
        if (request.channelId() == null) {
            throw new IllegalArgumentException("channelId가 필요합니다.");
        }

        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @PatchMapping(value = "/{readStatusId}")
    public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId,
        @RequestBody ReadStatusUpdateRequest request) {
        if (readStatusId == null) {
            throw new IllegalArgumentException("readStatusId가 필요합니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("request가 필요합니다.");
        }

        ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }

        List<ReadStatus> allByUserId = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
    }
}
