package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/read-status")
public class ReadStatusController {
    private final ReadStatusService readStatusService;
    public ReadStatusController(ReadStatusService readStatusService) { this.readStatusService = readStatusService; }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest req) {
        if (req == null || req.userId() == null || req.channelId() == null) {
            throw new IllegalArgumentException("userId와 channelId는 필수");
        }
        ReadStatus saved = readStatusService.create(req);
        return ResponseEntity.created(URI.create("/api/read-status/" + saved.getId())).body(saved);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{readStatusId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> find(@PathVariable UUID readStatusId) {
        return ResponseEntity.ok(readStatusService.find(readStatusId));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/by-user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ReadStatus>> findAllByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{readStatusId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId,
                                             @RequestBody ReadStatusUpdateRequest req) {
        if (req == null) throw new IllegalArgumentException("Empty");
        ReadStatus updated = readStatusService.update(readStatusId, req);
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{readStatusId}"
    )
    public ResponseEntity<Void> delete(@PathVariable UUID readStatusId) {
        readStatusService.delete(readStatusId);
        return ResponseEntity.noContent().build();
    }
}
