package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readstatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request){
        Instant lastReadAt = request.lastReadAt() != null ? request.lastReadAt() : Instant.now();
        ReadStatus createStatus = readStatusService.create(request);
        return ResponseEntity.ok(createStatus);
    }

    @RequestMapping(path = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> updateReadStatus(@PathVariable("id") UUID id,
                                                       @RequestBody ReadStatusUpdateRequest request){
        ReadStatus updateStatus = readStatusService.update(id, request);
        return ResponseEntity.ok(updateStatus);
    }

    @RequestMapping(path ="/find/{userId}", method = RequestMethod.GET)
    public ResponseEntity<ReadStatus> find(@PathVariable UUID userId){
        ReadStatus readStatus = readStatusService.find(userId);
        return ResponseEntity.ok(readStatus);
    }
}
