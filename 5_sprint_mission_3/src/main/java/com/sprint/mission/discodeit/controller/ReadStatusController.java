package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt() != null ? request.lastReadAt() : Instant.now()
        );
        ReadStatus created = readStatusService.create(readStatus);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> update(@PathVariable UUID id, @RequestBody ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusService.findByUserIdAndChannelId(request.userId(), request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus가 존재하지 않습니다."));
        readStatus.readUpdate(request.lastReadAt());
        ReadStatus updated = readStatusService.update(readStatus);
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/findAllByUser/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUser(@PathVariable UUID userId) {
        List<ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatusList);
    }

}
