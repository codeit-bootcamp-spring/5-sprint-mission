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
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(value = "/update/{readStatusId}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId,
                                             @RequestBody ReadStatusUpdateRequest request) {
        ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable UUID userId) {
        List<ReadStatus> allByUserId = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
    }
}
