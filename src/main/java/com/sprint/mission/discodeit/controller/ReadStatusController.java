package com.sprint.mission.discodeit.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> createReadStatus(
            @RequestBody ReadStatusCreateRequest request) {
        ReadStatusResponse response = readStatusService.create(request);
        URI location = URI.create("/api/readStatuses/" + response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> getReadStatusByUser(@RequestParam UUID userId) {
        List<ReadStatusResponse> responses = readStatusService.getAllByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @RequestMapping(path = "/{readStatusId}", method = RequestMethod.GET)
    public ResponseEntity<ReadStatusResponse> getReadStatusById(@PathVariable UUID readStatusId) {
        ReadStatusResponse readStatusResponse = readStatusService.getById(readStatusId);
        if (readStatusResponse == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(readStatusResponse);
    }

    @RequestMapping(path = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponse> updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request) {
        ReadStatusResponse response = readStatusService.updateById(readStatusId, request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/{readStatusId}", method = RequestMethod.DELETE)
    public ResponseEntity<ReadStatusResponse> deleteReadStatus(@PathVariable UUID readStatusId) {
        ReadStatusResponse response = readStatusService.delete(readStatusId);
        return ResponseEntity.ok(response);
    }
}