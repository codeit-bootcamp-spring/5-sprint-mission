package com.codeit.mission.discodeit.controller;

import com.codeit.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.codeit.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readstatus")
public class MessageReceptionController {

    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
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

    @RequestMapping(value = "/update/{readStatusId}", method = RequestMethod.PUT)
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

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }

        List<ReadStatus> allByUserId = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
    }
}
