package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.sub.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(value = "", method = POST)
    public ResponseEntity<ReadStatus> createReadStatus(
            @RequestBody ReadStatusCreateRequest readStatusCreateRequest
    ) {
        ReadStatus readStatus = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.ok(readStatus);
    }

    @RequestMapping(value = "/{readStatusId}", method = PATCH)
    public ResponseEntity<ReadStatus> updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatus readStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.ok(readStatus);
    }

    @RequestMapping(value = "/user/{userId}", method = GET)
    public ResponseEntity<List<ReadStatus>> getReadStatusesByUserId(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
