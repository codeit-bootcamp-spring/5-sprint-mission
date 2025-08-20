package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/read-statuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    public ReadStatusController(@Qualifier("basicReadStatusService") ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatusResponse response = readStatusService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{statusId}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatusResponse> updateReadStatus(@PathVariable UUID statusId, @RequestBody ReadStatusUpdateRequest request) {
        if (request.getId() == null || !statusId.equals(request.getId())) {
            throw new IllegalArgumentException("ReadStatus ID in path does not match ID in request body.");
        }
        ReadStatusResponse response = readStatusService.update(request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> getReadStatusesByUserId(@PathVariable UUID userId) {
        List<ReadStatusResponse> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatuses);
    }
}
