package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatus newReadStatus = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReadStatus);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID id, @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus updateReadStatus = readStatusService.update(id, readStatusUpdateRequest);
        return ResponseEntity.ok(updateReadStatus);
    }

    @RequestMapping(value = "/findAllByUser/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUser(@PathVariable UUID userId) {
        List<ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatusList);
    }

}
