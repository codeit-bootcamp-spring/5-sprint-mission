package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(path = "/read-status", method = RequestMethod.POST,
                        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request){
        ReadStatus created = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(path = "/read-status/{readStatusId}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadStatus> updateReadStatus(@PathVariable UUID readStatusId,
                                                       @RequestBody ReadStatusUpdateRequest request) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    @RequestMapping(path = "/users/{userId}/read-statuses", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> listReadStatusesByUser(@PathVariable UUID userId){
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

}
